package org.gudelker

interface Lexer {
    fun lex(input: String): List<Token>

}