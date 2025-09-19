package org.gudelker.lexer

import org.gudelker.resulttokenizers.LexerError
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.sourcereader.SourceReader
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

// Resultado que devuelve el StreamingLexer
sealed class StreamingLexerResult {
    data class TokenBatch(val tokens: List<Token>) : StreamingLexerResult()
    data class Error(val message: String) : StreamingLexerResult()
    object Finished : StreamingLexerResult()
}

// StreamingLexer que usa DefaultLexer como base pero genera tokens de a lotes
class StreamingLexer(private val defaultLexer: DefaultLexer) {
    private lateinit var sourceReader: SourceReader
    private var currentPosition = Position()
    private var currentWord = ""
    private var isFinished = false
    private var hasError = false
    private var errorMessage = ""
    private var initialized = false

    // Inicializa el StreamingLexer con un SourceReader
    fun initialize(sourceReader: SourceReader) {
        this.sourceReader = sourceReader
        this.currentPosition = Position()
        this.currentWord = ""
        this.isFinished = false
        this.hasError = false
        this.errorMessage = ""
        this.initialized = true
    }

    // Genera el siguiente token individual desde el sourceReader
    private fun nextSingleToken(): Token? {
        if (!initialized) {
            hasError = true
            errorMessage = "StreamingLexer not initialized. Call initialize(sourceReader) first."
            return null
        }

        if (isFinished || hasError) return null

        try {
            val rules = defaultLexer.getRules()

            while (!sourceReader.isEOF()) {
                val newChar = sourceReader.next().toString()
                currentWord += newChar
                val nextChar = sourceReader.peek()

                val matchingRule = rules.firstOrNull { it.matches(currentWord, nextChar) }

                if (matchingRule != null) {
                    val posWithNewOffset = defaultLexer.changingOffSet(currentPosition, currentWord)
                    val pos = posWithNewOffset.copy()
                    val tokenResult = matchingRule.generateToken(emptyList(), currentWord, pos)

                    when (tokenResult) {
                        is ValidToken -> {
                            // Resetear para el próximo token
                            currentWord = ""
                            currentPosition = defaultLexer.advancePosition(posWithNewOffset, nextChar)

                            // Devolver el primer token generado
                            if (tokenResult.tokens.isNotEmpty()) {
                                return tokenResult.tokens.first()
                            }
                            // Si no genera token, continuar con el siguiente carácter
                        }
                        is LexerError -> {
                            hasError = true
                            errorMessage = tokenResult.errMessage + ". Error at line ${pos.startLine}"
                            return null
                        }
                    }
                }
                // Si no hay regla que coincida, continuar acumulando caracteres
            }

            // Llegamos al final del archivo
            if (currentWord.isNotEmpty()) {
                hasError = true
                errorMessage = "Unexpected character sequence at end of file: $currentWord"
                return null
            }

            // Generar token EOF
            isFinished = true
            return Token(TokenType.EOF, "EOF", currentPosition)

        } catch (e: Exception) {
            hasError = true
            errorMessage = "Exception during lexing: ${e.message}"
            return null
        }
    }

    // Devuelve el siguiente lote de hasta N tokens
    fun nextBatch(batchSize: Int = 5): StreamingLexerResult {
        if (!initialized) {
            return StreamingLexerResult.Error("StreamingLexer not initialized. Call initialize(sourceReader) first.")
        }

        if (hasError) {
            return StreamingLexerResult.Error(errorMessage)
        }

        if (isFinished) {
            return StreamingLexerResult.Finished
        }

        val batch = mutableListOf<Token>()

        for (i in 0 until batchSize) {
            val token = nextSingleToken()

            if (hasError) {
                return StreamingLexerResult.Error(errorMessage)
            }

            if (token == null) {
                break
            }

            batch.add(token)

            // Si encontramos EOF, terminamos
            if (token.getType() == TokenType.EOF) {
                isFinished = true
                break
            }
        }

        return if (batch.isEmpty()) {
            StreamingLexerResult.Finished
        } else {
            StreamingLexerResult.TokenBatch(batch)
        }
    }

    // Verifica si hay más tokens disponibles
    fun hasMore(): Boolean {
        return initialized && !hasError && !isFinished
    }



    // Reinicia el lexer (requiere un nuevo sourceReader)
    fun reset(newSourceReader: SourceReader) {
        initialize(newSourceReader)
    }
}