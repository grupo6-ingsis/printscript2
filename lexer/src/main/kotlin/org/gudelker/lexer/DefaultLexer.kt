package org.gudelker.lexer

import org.gudelker.resulttokenizers.LexerError
import org.gudelker.resultlexer.LexerResult
import org.gudelker.resultlexer.LexerSyntaxError
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.resultlexer.ValidTokens
import org.gudelker.rules.RuleTokenizer
import org.gudelker.sourcereader.SourceReader
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType
import kotlin.collections.plus

class DefaultLexer(
    private val rules: List<RuleTokenizer>,
) : Lexer {
    override fun lex(sourceReader: SourceReader): LexerResult {
        fun lexRecursive(
            actualWord: String,
            tokensList: List<Token>,
            startPos: Position,
        ): LexerResult {
            if (sourceReader.isEOF()) {
                val finalTokens = tokensList + Token(TokenType.EOF, "EOF", startPos)
                return ValidTokens(finalTokens)
            }
            val newChar = sourceReader.next().toString()
            val updatedWord = actualWord + newChar
            val nextChar = sourceReader.peek()

            val matchingRule = rules.firstOrNull { it.matches(updatedWord, nextChar) }

            return if (matchingRule != null) {
                val posWithNewOffset = changingOffSet(startPos, updatedWord)
                val pos = posWithNewOffset.copy()
                val tokenResult = matchingRule.generateToken(tokensList, updatedWord, pos)

                when (tokenResult) {
                    is ValidToken -> {
                        lexRecursive(
                            "",
                            tokenResult.tokens,
                            advancePosition(
                                posWithNewOffset,
                                nextChar,
                            ),
                        )
                    }
                    is LexerError -> {
                        LexerSyntaxError(tokenResult.errMessage + ". Error at line ${pos.startLine}")
                    }
                }
            } else {
                lexRecursive(updatedWord, tokensList, startPos)
            }
        }

        return lexRecursive("", listOf(), Position())
    }

    fun advancePosition(
        position: Position,
        char: Char?,
    ): Position =
        when (char) {
            '\n', '\r' ->
                position.copy(
                    startOffset = position.endOffset + 1,
                    endOffset = position.endOffset + 1,
                    startLine = position.endLine + 1,
                    startColumn = 0,
                    endColumn = 0,
                    endLine = position.endLine + 1,
                )
            else ->
                position.copy(
                    startOffset = position.endOffset + 1,
                    endOffset = position.endOffset + 1,
                    endColumn = position.endColumn + 1,
                    startColumn = position.endColumn + 1,
                )
        }

    fun changingOffSet(
        position: Position,
        actualWord: String,
    ): Position =
        position.copy(
            endOffset = position.endOffset + actualWord.length,
        )

    fun getRules(): List<RuleTokenizer> = rules
}
