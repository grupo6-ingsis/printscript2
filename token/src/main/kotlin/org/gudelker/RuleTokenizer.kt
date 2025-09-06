package org.gudelker

import org.gudelker.result.TokenResult

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
