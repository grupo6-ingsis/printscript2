package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

class BooleanTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return (
            actualWord == "true" ||
                actualWord == "false"
        ) &&
            (
                nextChar == null || nextChar.isWhitespace() ||
                    nextChar == ',' ||
                    nextChar == ';' || nextChar == ')'
            )
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.BOOLEAN, actualWord, position)
        return ValidToken(newList)
    }
}
