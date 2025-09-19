package org.gudelker.rules

import org.gudelker.resulttokenizers.LexerError
import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.token.Position
import org.gudelker.token.Token

class ProhibitedSymbolDoubleTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        val invalidDoublePattern2 = "(\\d+\\.|\\d+[a-zA-Z]+)".toRegex()

        return actualWord.matches(invalidDoublePattern2) &&
            (nextChar == null || !nextChar.isDigit())
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        return LexerError("Unsupported format: $actualWord", position)
    }
}
