package org.gudelker.rules

import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token

class WhitespaceTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean = actualWord == " "

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult = ValidToken(tokens)
}
