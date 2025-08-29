package org.gudelker.result

class LexerSyntaxError(
    val messageError: String,
) : LexerResult {
    fun getError(): String = messageError
}
