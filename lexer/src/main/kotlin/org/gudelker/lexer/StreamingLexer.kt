package org.gudelker.lexer

import org.gudelker.resulttokenizers.LexerError
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.sourcereader.SourceReader
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

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
                            currentWord = ""
                            currentPosition = defaultLexer.advancePosition(posWithNewOffset, nextChar)

                            if (tokenResult.tokens.isNotEmpty()) {
                                return tokenResult.tokens.first()
                            }
                        }
                        is LexerError -> {
                            hasError = true
                            errorMessage = "${tokenResult.errMessage}. Error at line ${pos.startLine}"
                            return null
                        }
                    }
                }
            }

            // EOF reached
            if (currentWord.isNotEmpty()) {
                hasError = true
                errorMessage = "Unexpected character sequence at end: $currentWord"
                return null
            }

            isFinished = true
            return Token(TokenType.EOF, "EOF", currentPosition)
        } catch (e: Exception) {
            hasError = true
            errorMessage = "Lexing error: ${e.message}"
            return null
        }
    }

    fun hasMore(): Boolean = initialized && !hasError && !isFinished

    fun reset(newSourceReader: SourceReader) {
        initialize(newSourceReader)
    }
}
