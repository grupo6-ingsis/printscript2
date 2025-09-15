package org.gudelker.rules

import org.gudelker.result.LexerError
import org.gudelker.result.TokenResult
import org.gudelker.token.Position
import org.gudelker.token.Token

class NotLineAfterSemicolonTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord == ";" && !(nextChar == '\n' || nextChar == '\r' || nextChar == null)
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        return LexerError("Expected new line after semicolon", position)
    }
}
