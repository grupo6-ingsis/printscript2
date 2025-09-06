package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.result.LexerError
import org.gudelker.result.TokenResult

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
