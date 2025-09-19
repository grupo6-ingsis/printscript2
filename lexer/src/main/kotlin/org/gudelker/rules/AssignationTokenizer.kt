package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class AssignationTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord == "=" && (nextChar != '=')
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.ASSIGNATION, actualWord, position)
        return ValidToken(newList)
    }
}
