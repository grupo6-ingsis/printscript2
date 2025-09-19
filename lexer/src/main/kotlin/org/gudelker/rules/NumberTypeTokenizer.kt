package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class NumberTypeTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return (actualWord == "Number" || actualWord == "number") &&
            (nextChar == null || nextChar.isWhitespace() || nextChar == '=' || nextChar == ';')
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newToken = Token(TokenType.TYPE, actualWord, position)
        val newList = tokens + newToken
        return ValidToken(newList)
    }
}
