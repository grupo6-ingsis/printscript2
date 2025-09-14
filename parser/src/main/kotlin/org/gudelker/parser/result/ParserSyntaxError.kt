package org.gudelker.parser.result

data class ParserSyntaxError(
    private val errorMessage: String,
) : ParserResult {
    fun getError(): String = errorMessage
}
