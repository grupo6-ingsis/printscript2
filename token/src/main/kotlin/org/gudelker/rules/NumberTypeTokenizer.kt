package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token

class NumberTypeTokenizer : RuleTokenizer {
    override fun matches(actualWord: String, nextChar: Char?): Boolean {
        TODO("Not yet implemented")
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position
    ): List<Token> {
        TODO("Not yet implemented")
    }
}