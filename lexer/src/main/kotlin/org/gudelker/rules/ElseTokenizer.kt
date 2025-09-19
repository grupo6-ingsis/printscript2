package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class ElseTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord == "else" && (nextChar == null || nextChar.isWhitespace() || nextChar == '{' || nextChar == '\n')
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.ELSE_KEYWORD, actualWord, position)
        return ValidToken(newList)
    }
}
