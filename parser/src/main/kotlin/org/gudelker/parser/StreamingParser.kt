package org.gudelker.parser

import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.interfaces.Statement
import org.gudelker.token.Token
import org.gudelker.token.TokenType
import kotlin.text.matches

sealed class StreamingParserResult {
    data class StatementParsed(val statement: Statement) : StreamingParserResult()

    data class Error(val message: String) : StreamingParserResult()

    object Finished : StreamingParserResult()
}

class StreamingParser(private val defaultParser: DefaultParser) {
    private val tokenBuffer = mutableListOf<Token>()
    private var isFinished = false
    private var hasError = false
    private var errorMessage = ""

    fun addTokens(tokens: List<Token>) {
        if (hasError || isFinished) return
        tokenBuffer.addAll(tokens)
    }

    fun nextStatement(): StreamingParserResult {
        if (hasError) {
            return StreamingParserResult.Error(errorMessage)
        }

        if (isFinished) {
            return StreamingParserResult.Finished
        }

        // Si tenemos EOF pero no statements, terminamos
        if (tokenBuffer.any { it.getType() == TokenType.EOF } && tokenBuffer.size <= 1) {
            isFinished = true
            return StreamingParserResult.Finished
        }

        return tryParseStatement()
    }

    private fun tryParseStatement(): StreamingParserResult {
        if (tokenBuffer.isEmpty()) {
            return StreamingParserResult.Error("No tokens available")
        }

        try {
            val tokenStream = TokenStream(tokenBuffer.toList())
            val rules = defaultParser.getRules()

            for (rule in rules) {
                val testStream = TokenStream(tokenBuffer.toList())

                try {
                    if (rule.matches(testStream)) {
                        val parseResult = rule.parse(testStream)
                        val tokensUsed = parseResult.tokenStream.getCurrentIndex()
                        val lastUsedToken = tokenBuffer.getOrNull(tokensUsed - 1)
                        val bufferEndsAfterBlock = lastUsedToken?.getType() == TokenType.CLOSE_BRACKET && tokensUsed == tokenBuffer.size
                        if (bufferEndsAfterBlock) {
                            return StreamingParserResult.Error("Need more tokens")
                        }

                        when (val result = parseResult.parserResult) {
                            is ValidStatementParserResult -> {
                                val tokensUsed = parseResult.tokenStream.getCurrentIndex()
                                repeat(tokensUsed) {
                                    if (tokenBuffer.isNotEmpty()) {
                                        tokenBuffer.removeAt(0)
                                    }
                                }
                                return StreamingParserResult.StatementParsed(result.getStatement())
                            }
                            is ParserSyntaxError -> {
                                if (tokenBuffer.any { it.getType() == TokenType.EOF }) {
                                    errorMessage = result.getError()
                                    hasError = true
                                    return StreamingParserResult.Error(errorMessage)
                                } else {
                                    return StreamingParserResult.Error("Need more tokens")
                                }
                            }
                            else -> {
                                return if (tokenBuffer.any { it.getType() == TokenType.EOF }) {
                                    hasError = true
                                    errorMessage = "Cannot parse remaining tokens"
                                    StreamingParserResult.Error(errorMessage)
                                } else {
                                    StreamingParserResult.Error("Need more tokens")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (isInsufficientTokensError(e)) {
                        return if (tokenBuffer.any { it.getType() == TokenType.EOF }) {
                            hasError = true
                            errorMessage = "Cannot parse remaining tokens: ${e.message}"
                            StreamingParserResult.Error(errorMessage)
                        } else {
                            StreamingParserResult.Error("Need more tokens")
                        }
                    }
                    continue
                }
            }

            val currentToken = tokenBuffer.firstOrNull()
            if (currentToken?.getType() == TokenType.EOF) {
                isFinished = true
                return StreamingParserResult.Finished
            }

            hasError = true
            errorMessage = "No valid rule for token: ${currentToken?.getValue()}"
            return StreamingParserResult.Error(errorMessage)
        } catch (e: Exception) {
            hasError = true
            errorMessage = "Parse error: ${e.message}"
            return StreamingParserResult.Error(errorMessage)
        }
    }

    private fun isInsufficientTokensError(e: Exception): Boolean {
        val message = e.message?.lowercase() ?: ""
        return message.contains("token") ||
            message.contains("eof") ||
            message.contains("end") ||
            message.contains("null")
    }

    fun hasMore(): Boolean = !hasError && !isFinished && tokenBuffer.isNotEmpty()

    fun getBufferSize(): Int = tokenBuffer.size
}
