package org.gudelker

import org.gudelker.result.Result

interface Lexer {
    fun lex(input: String): Result
}