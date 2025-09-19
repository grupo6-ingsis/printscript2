package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

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
