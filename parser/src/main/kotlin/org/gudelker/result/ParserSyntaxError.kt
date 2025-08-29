package org.gudelker.result

class ParserSyntaxError(
    private val errorMessage: String,
) : ParserResult {
    fun getError(): String = errorMessage
}
