package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class PrintTokenizer : RuleTokenizer {
    private val functions = setOf("println", "read")

    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return functions.contains(actualWord) &&
            (nextChar == null || (!nextChar.isLetterOrDigit() && nextChar != '_'))
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): List<Token> {
        return tokens + Token(TokenType.FUNCTION, actualWord, position)
    }
}
