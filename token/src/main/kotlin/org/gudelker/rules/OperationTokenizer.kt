package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class OperationTokenizer : RuleTokenizer {
    override fun matches(actualWord: String, nextChar: Char?): Boolean {
        return (actualWord == "+" || actualWord == "-" || actualWord == "*" || actualWord == "/")
                && (nextChar == null || !nextChar.isDigit() || nextChar.isWhitespace())
    }

    override fun generateToken(tokens: List<Token>, actualWord: String, position: Position): List<Token> {
        val newList = tokens + Token(TokenType.OPERATOR, actualWord, position)
        return newList
    }
}