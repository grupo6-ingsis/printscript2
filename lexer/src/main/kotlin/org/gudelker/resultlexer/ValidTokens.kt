package org.gudelker.resultlexer

import org.gudelker.token.Token

data class ValidTokens(
    val value: List<Token>,
) : LexerResult {
    fun getList(): List<Token> = value
}
