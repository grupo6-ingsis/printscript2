package org.gudelker.rules

import org.gudelker.result.LexerError
import org.gudelker.result.TokenResult
import org.gudelker.token.Position
import org.gudelker.token.Token

class NotSpaceOperationTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        val isAdditional =
            !actualWord[actualWord.length - 1].isWhitespace()
        val nextCharIsOperation = nextChar == '+' || nextChar == '-' || nextChar == '*' || nextChar == '/'
        val isOperation = actualWord == "+" || actualWord == "-" || actualWord == "*" || actualWord == "/"
        val nextCharIsDigit: Boolean = !(nextChar?.isWhitespace())!!
        return (isAdditional && nextCharIsOperation) || (isOperation && nextCharIsDigit)
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        return LexerError("Missing space between number and operation (necessarily for this version)", position)
    }
}
