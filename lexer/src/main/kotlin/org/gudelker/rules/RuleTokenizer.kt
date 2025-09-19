package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.token.Position
import org.gudelker.token.Token

interface RuleTokenizer {
    fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean

    fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult
}
