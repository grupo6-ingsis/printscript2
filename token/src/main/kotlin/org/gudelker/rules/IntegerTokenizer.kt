package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

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
