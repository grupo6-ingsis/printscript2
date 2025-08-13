package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class ProhibitedSymbolDoubleTokenizer: RuleTokenizer {
    override fun matches(actualWord: String, nextChar: Char?): Boolean {
        val invalidDoublePattern = "\\d+\\.".toRegex()

        return actualWord.matches(invalidDoublePattern) &&
                (nextChar == null || !nextChar.isDigit())
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position
    ): List<Token> {
        val mutableCopy = tokens.toMutableList()
        mutableCopy.add(Token(TokenType.UNKNOWN, actualWord, position))

        val newImmutableList: List<Token> = mutableCopy.toList()

        return newImmutableList
    }
}