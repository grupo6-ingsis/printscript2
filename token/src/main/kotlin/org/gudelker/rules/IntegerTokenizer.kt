package org.gudelker.rules

import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class IntegerTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        // Use regex matching correctly to check for integers
        val digitPattern = "\\d+".toRegex()

        return actualWord.matches(digitPattern) &&
            (
                nextChar == null || nextChar == '=' || nextChar == '>' ||
                    nextChar == '<' || nextChar == ';' || nextChar == ')' ||
                    nextChar == ',' || nextChar == ' ' || nextChar == '\n' ||
                    nextChar == '\t' || (nextChar == '+' || nextChar == '-' || nextChar == '*' || nextChar == '/')
            )
    }

    override fun generateToken(
        tokens: List<Token>,
        actualWord: String,
        position: Position,
    ): TokenResult {
        val newList = tokens + Token(TokenType.NUMBER, actualWord, position)
        return ValidToken(newList)
    }
}
