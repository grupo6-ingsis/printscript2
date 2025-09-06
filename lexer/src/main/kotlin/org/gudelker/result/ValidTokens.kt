package org.gudelker.result

import org.gudelker.Token

data class ValidTokens(
    val value: List<Token>,
) : LexerResult {
    fun getList(): List<Token> = value
}
