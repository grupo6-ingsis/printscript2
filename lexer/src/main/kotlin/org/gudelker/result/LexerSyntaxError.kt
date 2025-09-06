package org.gudelker.result

data class LexerSyntaxError(
    val messageError: String,
) : LexerResult {
    fun getError(): String = messageError
}
