package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class PrintTokenizer : RuleTokenizer {
    private val functions = setOf("println")

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
    ): TokenResult {
        val newList = tokens + Token(TokenType.FUNCTION, actualWord, position)
        return ValidToken(newList)
    }
}
