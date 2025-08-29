package org.gudelker.result

import org.gudelker.Token

class ValidTokens(
    val value: List<Token>,
) : LexerResult {
    fun getList(): List<Token> = value
}
