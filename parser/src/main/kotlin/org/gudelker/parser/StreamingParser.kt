package org.gudelker.parser

import org.gudelker.lexer.StreamingLexer
import org.gudelker.lexer.StreamingLexerResult
import org.gudelker.parser.result.ParserResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.rule.SyntaxParRule
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.interfaces.Statement
import org.gudelker.token.Token
import org.gudelker.token.TokenType

// Resultado que devuelve el StreamingParser
sealed class StreamingParserResult {
    data class StatementParsed(val statement: Statement, val tokensUsed: Int) : StreamingParserResult()
    data class NeedMoreTokens(val message: String) : StreamingParserResult()
    data class Error(val message: String) : StreamingParserResult()
    object Finished : StreamingParserResult()
}

// Buffer de tokens dinámico que se expande según necesidad
class DynamicTokenBuffer {
    private val tokens = mutableListOf<Token>()
    private var consumedTokens = 0

    fun addTokens(newTokens: List<Token>) {
        tokens.addAll(newTokens)
    }

    fun getCurrentTokens(): List<Token> {
        return if (consumedTokens < tokens.size) {
            tokens.subList(consumedTokens, tokens.size)
        } else {
            emptyList()
        }
    }

    fun getAllTokens(): List<Token> = tokens

    fun markTokensAsConsumed(count: Int) {
        consumedTokens = minOf(consumedTokens + count, tokens.size)

        // Limpiar tokens ya consumidos para liberar memoria
        if (consumedTokens > 50) { // Mantener un buffer pequeño
            val tokensToRemove = consumedTokens - 10
            repeat(tokensToRemove) {
                if (tokens.isNotEmpty()) tokens.removeAt(0)
            }
            consumedTokens -= tokensToRemove
        }
    }

    fun hasTokens(): Boolean = getCurrentTokens().isNotEmpty()

    fun hasEOF(): Boolean = tokens.any { it.getType() == TokenType.EOF }

    fun size(): Int = getCurrentTokens().size

    fun clear() {
        tokens.clear()
        consumedTokens = 0
    }
}

// StreamingParser que usa DefaultParser como base
class StreamingParser(
    private val defaultParser: DefaultParser,
    private val streamingLexer: StreamingLexer,
    private val maxTokensPerAttempt: Int = 50 // Máximo de tokens a acumular antes de fallar
) {
    private val tokenBuffer = DynamicTokenBuffer()
    private var isFinished = false
    private var hasError = false
    private var errorMessage = ""

    // Intenta parsear el siguiente statement
    fun nextStatement(): StreamingParserResult {
        if (hasError) {
            return StreamingParserResult.Error(errorMessage)
        }

        if (isFinished) {
            return StreamingParserResult.Finished
        }

        // Intentar parsear con tokens disponibles
        var attempts = 0
        val maxAttempts = 10 // Evitar bucles infinitos

        while (attempts < maxAttempts) {
            attempts++

            // Si no hay tokens suficientes, pedir más del lexer
            if (!tokenBuffer.hasTokens() ||
                (tokenBuffer.size() < 3 && !tokenBuffer.hasEOF())) { // Mínimo 3 tokens para intentar

                when (val lexerResult = streamingLexer.nextBatch(5)) {
                    is StreamingLexerResult.TokenBatch -> {
                        tokenBuffer.addTokens(lexerResult.tokens)

                        // Si recibimos EOF, marcar como candidato a terminar
                        if (lexerResult.tokens.any { it.getType() == TokenType.EOF }) {
                            // No marcar finished aún, intentar parsear lo que queda
                        }
                    }
                    is StreamingLexerResult.Error -> {
                        hasError = true
                        errorMessage = "Lexer error: ${lexerResult.message}"
                        return StreamingParserResult.Error(errorMessage)
                    }
                    StreamingLexerResult.Finished -> {
                        // Si no hay más tokens del lexer y el buffer está vacío, terminamos
                        if (!tokenBuffer.hasTokens()) {
                            isFinished = true
                            return StreamingParserResult.Finished
                        }
                    }
                }
            }

            // Intentar parsear con los tokens actuales
            val currentTokens = tokenBuffer.getCurrentTokens()
            if (currentTokens.isEmpty()) {
                isFinished = true
                return StreamingParserResult.Finished
            }

            // Crear TokenStream temporal para el parser
            val tokenStream = TokenStream(currentTokens)

            try {
                // Intentar parsear un solo statement
                val parseResult = tryParseOneStatement(tokenStream)

                when (parseResult) {
                    is SingleStatementParseResult.Success -> {
                        // Éxito! Marcar tokens como consumidos
                        tokenBuffer.markTokensAsConsumed(parseResult.tokensUsed)
                        return StreamingParserResult.StatementParsed(
                            parseResult.statement,
                            parseResult.tokensUsed
                        )
                    }
                    is SingleStatementParseResult.NeedMoreTokens -> {
                        // Necesita más tokens, continuar el loop
                        if (tokenBuffer.hasEOF()) {
                            // Si ya tenemos EOF y no puede parsear, es un error
                            hasError = true
                            errorMessage = "Cannot parse remaining tokens: ${parseResult.message}"
                            return StreamingParserResult.Error(errorMessage)
                        }

                        if (tokenBuffer.size() >= maxTokensPerAttempt) {
                            hasError = true
                            errorMessage = "Too many tokens accumulated without successful parse: ${tokenBuffer.size()}"
                            return StreamingParserResult.Error(errorMessage)
                        }

                        continue // Pedir más tokens
                    }
                    is SingleStatementParseResult.Error -> {
                        hasError = true
                        errorMessage = parseResult.message
                        return StreamingParserResult.Error(errorMessage)
                    }
                }

            } catch (e: Exception) {
                hasError = true
                errorMessage = "Unexpected parser error: ${e.message}"
                return StreamingParserResult.Error(errorMessage)
            }
        }

        // Si llegamos aquí, demasiados intentos
        hasError = true
        errorMessage = "Too many parse attempts without success"
        return StreamingParserResult.Error(errorMessage)
    }

    // Intenta parsear exactamente un statement
    private fun tryParseOneStatement(tokenStream: TokenStream): SingleStatementParseResult {
        val initialIndex = tokenStream.getCurrentIndex()
        val rules = defaultParser.getRules() // Necesitamos agregar este método al DefaultParser

        for (rule in rules) {
            // Crear una copia del tokenStream para no modificar el original
            val testStream = TokenStream(tokenStream.getTokens())
            // Avanzar al mismo índice
            repeat(initialIndex) { testStream.next() }

            try {
                if (rule.matches(testStream)) {
                    val parseResult = rule.parse(testStream)

                    when (parseResult.parserResult) {
                        is ValidStatementParserResult -> {
                            val tokensUsed = parseResult.tokenStream.getCurrentIndex() - initialIndex
                            return SingleStatementParseResult.Success(
                                parseResult.parserResult.getStatement(),
                                tokensUsed
                            )
                        }
                        else -> {
                            return SingleStatementParseResult.Error(
                                "Parser rule failed: ${parseResult.parserResult}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Si falla por falta de tokens, podría necesitar más
                if (isTokenRelatedError(e)) {
                    return SingleStatementParseResult.NeedMoreTokens(
                        "Rule ${rule.javaClass.simpleName} needs more tokens: ${e.message}"
                    )
                }
                // Otros errores son reales errores de parse
                continue
            }
        }

        // No se encontró regla que coincida
        val currentToken = tokenStream.current()
        return if (currentToken?.getType() == TokenType.EOF) {
            SingleStatementParseResult.Error("Unexpected EOF")
        } else {
            SingleStatementParseResult.NeedMoreTokens(
                "No matching rule for token: ${currentToken?.getValue()}"
            )
        }
    }

    // Detecta si una excepción es por falta de tokens
    private fun isTokenRelatedError(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("token") ||
                message.contains("eof") ||
                message.contains("end") ||
                message.contains("null")
    }

    // Verifica si puede continuar parseando
    fun hasMore(): Boolean {
        return !hasError && !isFinished && (tokenBuffer.hasTokens() || streamingLexer.hasMore())
    }

    // Obtiene el estado actual
    fun getStatus(): String {
        return when {
            hasError -> "Error: $errorMessage"
            isFinished -> "Finished"
            else -> "Parsing (buffer: ${tokenBuffer.size()} tokens)"
        }
    }

    // Procesa todos los statements automáticamente
    fun processAllStatements(processor: (Statement) -> Boolean): Boolean {
        while (hasMore()) {
            when (val result = nextStatement()) {
                is StreamingParserResult.StatementParsed -> {
                    val shouldContinue = processor(result.statement)
                    if (!shouldContinue) return true
                }
                is StreamingParserResult.Error -> {
                    println("Parser error: ${result.message}")
                    return false
                }
                is StreamingParserResult.NeedMoreTokens -> {
                    // Esto no debería pasar con nextStatement(), pero por si acaso
                    println("Unexpected: ${result.message}")
                    continue
                }
                StreamingParserResult.Finished -> break
            }
        }
        return true
    }
}

// Resultado interno para parsing de un solo statement
sealed class SingleStatementParseResult {
    data class Success(val statement: Statement, val tokensUsed: Int) : SingleStatementParseResult()
    data class NeedMoreTokens(val message: String) : SingleStatementParseResult()
    data class Error(val message: String) : SingleStatementParseResult()
}