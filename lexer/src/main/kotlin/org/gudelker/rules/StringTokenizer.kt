package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class StringTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        if (actualWord.length < 2) {
            return false
        }

        val firstChar = actualWord[0]
        val firstCharIsQuotationMark = firstChar != '"' && firstChar != '\''
        if (firstCharIsQuotationMark) {
            return false
        }
        return actualWord.last() == firstChar
    }

    private fun matchesRegex(
        actualWord: String,
        regex: String,
    ) = actualWord.matches(regex.toRegex())

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.STRING, actualWord, position)
        return ValidToken(newList)
    }
}
