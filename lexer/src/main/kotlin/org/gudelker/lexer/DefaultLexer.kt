package org.gudelker.lexer

import org.gudelker.resultlexer.LexerResult
import org.gudelker.resultlexer.LexerSyntaxError
import org.gudelker.resultlexer.ValidTokens
import org.gudelker.resulttokenizers.LexerError
import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.rules.RuleTokenizer
import org.gudelker.sourcereader.SourceReader
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType
import kotlin.collections.plus
import kotlin.text.matches

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
                return handleEOF(tokensList, startPos)
            }
            val newChar = sourceReader.next().toString()
            val updatedWord = actualWord + newChar
            val nextChar = sourceReader.peek()

            val matchingRule = findMatchingRule(updatedWord, nextChar)

            return if (matchingRule != null) {
                val posWithNewOffset = changingOffSet(startPos, updatedWord)
                processMatchingRule(
                    matchingRule,
                    tokensList,
                    updatedWord,
                    posWithNewOffset,
                    nextChar,
                    ::lexRecursive,
                )
            } else {
                lexRecursive(updatedWord, tokensList, startPos)
            }
        }

        return lexRecursive("", listOf(), Position())
    }

    private fun handleEOF(
        tokensList: List<Token>,
        startPos: Position,
    ): ValidTokens {
        val finalTokens = tokensList + Token(TokenType.EOF, "EOF", startPos)
        return ValidTokens(finalTokens)
    }

    private fun handleTokenResult(
        tokenResult: TokenResult,
        pos: Position,
        posWithNewOffset: Position,
        nextChar: Char?,
        lexRecursive: (String, List<Token>, Position) -> LexerResult,
    ): LexerResult =
        when (tokenResult) {
            is ValidToken -> lexRecursive("", tokenResult.tokens, advancePosition(posWithNewOffset, nextChar))
            is LexerError -> LexerSyntaxError(tokenResult.errMessage + ". Error at line ${pos.startLine}")
        }

    private fun findMatchingRule(
        updatedWord: String,
        nextChar: Char?,
    ): RuleTokenizer? = rules.firstOrNull { it.matches(updatedWord, nextChar) }

    private fun processMatchingRule(
        matchingRule: RuleTokenizer,
        tokensList: List<Token>,
        updatedWord: String,
        posWithNewOffset: Position,
        nextChar: Char?,
        lexRecursive: (String, List<Token>, Position) -> LexerResult,
    ): LexerResult {
        val pos = posWithNewOffset.copy()
        val tokenResult = matchingRule.generateToken(tokensList, updatedWord, pos)
        return handleTokenResult(tokenResult, pos, posWithNewOffset, nextChar, lexRecursive)
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
