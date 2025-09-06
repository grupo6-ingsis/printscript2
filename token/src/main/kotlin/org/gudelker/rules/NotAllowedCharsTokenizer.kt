package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.result.LexerError
import org.gudelker.result.TokenResult

class NotAllowedCharsTokenizer(
    private val notAllowedChars: Set<Char>,
    private val notAllowedWords: Set<String>,
) : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        return actualWord.length == 1 && actualWord[0] in notAllowedChars ||
            actualWord in notAllowedWords
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        return LexerError("Character '$actualWord' is not allowed in this version", position)
    }
}
