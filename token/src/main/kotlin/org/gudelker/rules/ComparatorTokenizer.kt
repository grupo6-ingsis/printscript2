package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

class ComparatorTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return (
            actualWord == ">" ||
                actualWord == "<"
        ) &&
            nextChar != '='
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.COMPARATOR, actualWord, position)
        return ValidToken(newList)
    }
}
