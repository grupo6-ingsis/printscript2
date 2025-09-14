package org.gudelker.parser.result

sealed interface ParserResult {
    fun isValid(): Boolean = this is Valid
}
