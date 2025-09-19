package org.gudelker.resultlexer

sealed interface LexerResult {
    fun isValid(): Boolean = this is ValidTokens
}
