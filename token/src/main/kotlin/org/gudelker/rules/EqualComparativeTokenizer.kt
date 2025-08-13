package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class EqualComparativeTokenizer: RuleTokenizer {
    override fun matches(actualWord: String, nextChar: Char?): Boolean {
        return actualWord == "==" || actualWord == "!=" || actualWord == "<=" || actualWord == ">=" || actualWord == "<" || actualWord == ">"
    }

    override fun generateToken(tokens: List<Token>, actualWord: String, position: Position): List<Token> {
        val mutableCopy = tokens.toMutableList()
        mutableCopy.add(Token(TokenType.COMPARATOR, actualWord, position))

        val newImmutableList: List<Token> = mutableCopy.toList()

        return newImmutableList
    }
}