package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class NumberTypeTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord == "Number" && (nextChar == null || nextChar.isWhitespace() || nextChar == '=' || nextChar == ';')
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): List<Token> {
        val newToken = Token(TokenType.TYPE, actualWord, position)
        val newList = tokens + newToken
        return newList
    }
}
