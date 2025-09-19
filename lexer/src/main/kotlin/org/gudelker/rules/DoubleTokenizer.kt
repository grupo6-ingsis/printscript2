package org.gudelker.rules

import org.gudelker.resulttokenizers.TokenResult
import org.gudelker.resulttokenizers.ValidToken
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class DoubleTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        val doublePattern = "\\d+\\.\\d+".toRegex()

        return actualWord.matches(doublePattern) &&
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
