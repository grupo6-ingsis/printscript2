package org.gudelker.result

sealed interface LexerResult {
    fun isValid(): Boolean = this is ValidTokens
}
