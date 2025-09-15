package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

class OperationTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return (actualWord == "+" || actualWord == "-" || actualWord == "*" || actualWord == "/") &&
            (nextChar == null || nextChar.isWhitespace() || nextChar.isDigit())
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.OPERATOR, actualWord, position)
        return ValidToken(newList)
    }
}
