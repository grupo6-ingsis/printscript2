package org.gudelker.result

sealed interface ParserResult {
    fun isValid(): Boolean = this is Valid
}
