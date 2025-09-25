package org.gudelker.lexer

import org.gudelker.resulttokenizers.LexerError
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.rules.RuleTokenizer
import org.gudelker.sourcereader.SourceReader
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType
import kotlin.collections.plusAssign

sealed class StreamingLexerResult {
    data class TokenBatch(val tokens: List<Token>) : StreamingLexerResult()

    data class Error(val message: String) : StreamingLexerResult()

    data object Finished : StreamingLexerResult()
}

class StreamingLexer(private val defaultLexer: DefaultLexer) {
    private lateinit var sourceReader: SourceReader
    private var currentPosition = Position()
    private var currentWord = ""
    private var isFinished = false
    private var hasError = false
    private var errorMessage = ""
    private var initialized = false

    fun initialize(sourceReader: SourceReader) {
        this.sourceReader = sourceReader
        this.currentPosition = Position()
        this.currentWord = ""
        this.isFinished = false
        this.hasError = false
        this.errorMessage = ""
        this.initialized = true
    }

    fun nextBatch(batchSize: Int = 10): StreamingLexerResult {
        if (!initialized) {
            return StreamingLexerResult.Error("StreamingLexer not initialized")
        }
        if (hasError) {
            return StreamingLexerResult.Error(errorMessage)
        }
        if (isFinished) {
            return StreamingLexerResult.Finished
        }
        val batch = mutableListOf<Token>()
        repeat(batchSize) {
            val token = nextSingleToken() ?: return@repeat

            batch.add(token)

            if (token.getType() == TokenType.EOF) {
                isFinished = true
                return@repeat
            }
        }
        return if (batch.isEmpty()) {
            StreamingLexerResult.Finished
        } else {
            StreamingLexerResult.TokenBatch(batch)
        }
    }

    private fun nextSingleToken(): Token? {
        if (isFinished || hasError) return null

        try {
            val rules = defaultLexer.getRules()

            while (!sourceReader.isEOF()) {
                val (currentWord, nextChar) = consumeNextChar()
                val matchingRule = findMatchingRule(rules, currentWord, nextChar)
                if (matchingRule != null) {
                    val posWithNewOffset = defaultLexer.changingOffSet(currentPosition, currentWord)
                    return processMatchingRule(matchingRule, emptyList(), currentWord, posWithNewOffset, nextChar)
                }
            }
            if (currentWord.isNotEmpty()) {
                return handleUnexpectedSequenceAtEnd()
            }
            return handleEOF()
        } catch (e: Exception) {
            return handleException(e)
        }
    }

    private fun processMatchingRule(
        matchingRule: RuleTokenizer,
        tokensList: List<Token>,
        currentWord: String,
        posWithNewOffset: Position,
        nextChar: Char?,
    ): Token? {
        val pos = posWithNewOffset.copy()
        val tokenResult = matchingRule.generateToken(tokensList, currentWord, pos)
        return when (tokenResult) {
            is ValidToken -> handleValidToken(tokenResult, posWithNewOffset, nextChar)
            is LexerError -> handleLexerError(tokenResult, pos)
        }
    }

    private fun findMatchingRule(
        rules: List<RuleTokenizer>,
        currentWord: String,
        nextChar: Char?,
    ): RuleTokenizer? = rules.firstOrNull { it.matches(currentWord, nextChar) }

    private fun handleValidToken(
        tokenResult: ValidToken,
        posWithNewOffset: Position,
        nextChar: Char?,
    ): Token? {
        currentWord = ""
        currentPosition = defaultLexer.advancePosition(posWithNewOffset, nextChar)
        return tokenResult.tokens.firstOrNull()
    }

    private fun handleLexerError(
        tokenResult: LexerError,
        pos: Position,
    ): Token? {
        hasError = true
        errorMessage = "${tokenResult.errMessage}. Error at line ${pos.startLine}"
        return null
    }

    private fun handleEOF(): Token? {
        isFinished = true
        return Token(TokenType.EOF, "EOF", currentPosition)
    }

    private fun handleUnexpectedSequenceAtEnd(): Token? {
        hasError = true
        errorMessage = "Unexpected character sequence at end: $currentWord"
        return null
    }

    private fun handleException(e: Exception): Token? {
        hasError = true
        errorMessage = "Lexing error: ${e.message}"
        return null
    }

    private fun consumeNextChar(): Pair<String, Char?> {
        val newChar = sourceReader.next().toString()
        currentWord += newChar
        val nextChar = sourceReader.peek()
        return Pair(currentWord, nextChar)
    }

    fun hasMore(): Boolean = initialized && !hasError && !isFinished

    fun reset(newSourceReader: SourceReader) {
        initialize(newSourceReader)
    }
}
