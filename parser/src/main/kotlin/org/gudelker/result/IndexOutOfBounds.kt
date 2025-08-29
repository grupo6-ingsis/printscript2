package org.gudelker.result

class IndexOutOfBounds(
    private val errorMessage: String,
) : ParserResult {
    fun getError(): String = errorMessage
}
