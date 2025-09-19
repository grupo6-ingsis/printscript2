package org.gudelker.resultlexer

data class LexerSyntaxError(
    val messageError: String,
) : LexerResult {
    fun getError(): String = messageError
}
