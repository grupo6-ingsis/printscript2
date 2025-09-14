package org.gudelker
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.rules.FormatterRule
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {
    @Test
    fun `test espacio antes de dos puntos en declaracion - con espacio`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position(1, 1, 1, 1, 1, 1)),
                Token(TokenType.IDENTIFIER, "x", Position(1, 2, 1, 1, 3, 3)),
                Token(TokenType.COLON, ":", Position(1, 1, 1, 1, 4, 4)),
                Token(TokenType.TYPE, "Number", Position(1, 1, 1, 1, 5, 5)),
                Token(TokenType.ASSIGNATION, "=", Position(1, 1, 1, 1, 6, 6)),
                Token(TokenType.NUMBER, "10", Position(1, 1, 1, 1, 7, 7)),
                Token(TokenType.SEMICOLON, ";", Position(1, 1, 1, 1, 8, 8)),
                Token(TokenType.EOF, "", Position(1, 1, 1, 1, 9, 9)),
            )

        val tokenStream = TokenStream(tokens)
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatter()

        val result = formatter.format(tokenStream, rules)

        assertEquals("let x : Number = 10;\n", result)
    }
}
