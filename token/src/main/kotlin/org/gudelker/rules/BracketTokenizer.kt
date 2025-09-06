package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

class BracketTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord == "{" || actualWord == "}"
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        if (actualWord == "{") {
            val newList = tokens + Token(TokenType.OPEN_BRACKET, actualWord, position)
            return ValidToken(newList)
        }
        val newList = tokens + Token(TokenType.CLOSE_BRACKET, actualWord, position)
        return ValidToken(newList)
    }
}
