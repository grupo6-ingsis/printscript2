package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class NotSpaceOperationTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        val isNumber = actualWord[actualWord.length - 1].isDigit()
        val nextCharIsOperation = nextChar == '+' || nextChar == '-' || nextChar == '*' || nextChar == '/'
        return isNumber && nextCharIsOperation
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): List<Token> {
        val newList = tokens + Token(TokenType.UNKNOWN, actualWord, position)
        return newList
    }
}
