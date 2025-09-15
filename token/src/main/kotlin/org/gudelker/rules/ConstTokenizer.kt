package org.gudelker.rules

import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class ConstTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord == "const" && (nextChar == null || nextChar.isWhitespace())
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.KEYWORD, actualWord, position)
        return ValidToken(newList)
    }
}
