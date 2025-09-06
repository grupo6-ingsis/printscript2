package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

class IdentifierTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        val regex = "[a-zA-Z_][a-zA-Z0-9_]*"

        val nextCharExtendsIdentifier =
            nextChar != null &&
                (nextChar.isLetterOrDigit() || nextChar == '_')
        return matchesRegex(actualWord, regex) && !nextCharExtendsIdentifier
    }

    private fun matchesRegex(
        actualWord: String,
        regex: String,
    ) = actualWord.matches(regex.toRegex())

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.IDENTIFIER, actualWord, position)
        return ValidToken(newList)
    }
}
