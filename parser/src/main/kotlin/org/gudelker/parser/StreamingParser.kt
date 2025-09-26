package org.gudelker.parser

import org.gudelker.parser.result.ParseResult
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

    data object Finished : StreamingParserResult()
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
        return parseWithRules() ?: handleNoValidRule()
    }

    private fun parseWithRules(): StreamingParserResult? {
        val rules = defaultParser.getRules()
        for (rule in rules) {
            val testStream = TokenStream(tokenBuffer.toList())
            try {
                if (rule.matches(testStream)) {
                    val parseResult = rule.parse(testStream)
                    return handleParseResult(parseResult)
                }
            } catch (e: Exception) {
                if (isInsufficientTokensError(e)) {
                    return handleInsufficientTokensError(e)
                }
                continue
            }
        }
        return null
    }

    private fun handleParseResult(parseResult: ParseResult): StreamingParserResult {
        val tokensUsed = parseResult.tokenStream.getCurrentIndex()
        val lastUsedToken = tokenBuffer.getOrNull(tokensUsed - 1)
        if (isBufferEndsAfterBlock(lastUsedToken, tokensUsed)) {
            return needMoreTokensError()
        }
        return handleParserResultType(parseResult.parserResult, tokensUsed)
    }

    private fun handleParserResultType(
        result: Any?,
        tokensUsed: Int,
    ): StreamingParserResult {
        return when (result) {
            is ValidStatementParserResult -> {
                removeUsedTokens(tokensUsed)
                StreamingParserResult.StatementParsed(result.getStatement())
            }
            is ParserSyntaxError -> {
                if (hasEOF()) {
                    setErrorAndReturn(result.getError())
                } else {
                    needMoreTokensError()
                }
            }
            else -> {
                if (hasEOF()) {
                    setErrorAndReturn("Cannot parse remaining tokens")
                } else {
                    needMoreTokensError()
                }
            }
        }
    }

    private fun isBufferEndsAfterBlock(
        lastUsedToken: Token?,
        tokensUsed: Int,
    ): Boolean {
        return lastUsedToken?.getType() == TokenType.CLOSE_BRACKET && tokensUsed == tokenBuffer.size
    }

    private fun removeUsedTokens(tokensUsed: Int) {
        repeat(tokensUsed) { if (tokenBuffer.isNotEmpty()) tokenBuffer.removeAt(0) }
    }

    private fun hasEOF(): Boolean = tokenBuffer.any { it.getType() == TokenType.EOF }

    private fun needMoreTokensError(): StreamingParserResult = StreamingParserResult.Error("Need more tokens")

    private fun setErrorAndReturn(message: String): StreamingParserResult {
        errorMessage = message
        hasError = true
        return StreamingParserResult.Error(errorMessage)
    }

    private fun handleInsufficientTokensError(e: Exception): StreamingParserResult {
        return if (tokenBuffer.any { it.getType() == TokenType.EOF }) {
            hasError = true
            errorMessage = "Cannot parse remaining tokens: ${e.message}"
            StreamingParserResult.Error(errorMessage)
        } else {
            StreamingParserResult.Error("Need more tokens")
        }
    }

    private fun handleNoValidRule(): StreamingParserResult {
        val currentToken = tokenBuffer.firstOrNull()
        if (currentToken?.getType() == TokenType.EOF) {
            isFinished = true
            return StreamingParserResult.Finished
        }
        hasError = true
        errorMessage = "No valid rule for token: ${currentToken?.getValue()}"
        return StreamingParserResult.Error(errorMessage)
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
