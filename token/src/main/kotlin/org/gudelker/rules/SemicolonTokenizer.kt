package org.gudelker.rules

import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class SemicolonTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord == ";"
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.SEMICOLON, actualWord, position)
        return ValidToken(newList)
    }
}
