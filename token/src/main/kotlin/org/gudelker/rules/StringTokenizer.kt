package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.TokenResult
import org.gudelker.result.ValidToken

class StringTokenizer : RuleTokenizer {
    override fun matches(
        actualWord: String,
        nextChar: Char?,
    ): Boolean {
        // Verificar que tenga al menos 2 caracteres (comillas de apertura y cierre)
        if (actualWord.length < 2) {
            return false
        }

        val firstChar = actualWord[0]
        val firstCharIsQuotationMark = firstChar != '"' && firstChar != '\''
        if (firstCharIsQuotationMark) {
            return false
        }

        // La cadena está completa si el último carácter es la misma comilla de inicio
        // y el siguiente carácter ya no forma parte de la cadena
        return actualWord.last() == firstChar
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
        val newList = tokens + Token(TokenType.STRING, actualWord, position)
        return ValidToken(newList)
    }
}
