package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

class StringTypeTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return (actualWord == "String") && (nextChar == null || nextChar.isWhitespace() || nextChar == '=' || nextChar == ';')
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
