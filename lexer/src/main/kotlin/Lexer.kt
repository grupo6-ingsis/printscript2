package org.gudelker.lexer

import org.gudelker.components.Position
import org.gudelker.components.Token

interface Lexer {
    fun lex(input: String): List<Token>

}