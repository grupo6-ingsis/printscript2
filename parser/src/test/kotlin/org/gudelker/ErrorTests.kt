package org.gudelker

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.parser.DefaultParserFactory
import org.gudelker.result.ParserSyntaxError
import org.gudelker.tokenstream.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ErrorTests {
    @Test
    fun `should return error for missing semicolon`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "10", Position()),
                // Falta el punto y coma
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(tokenStream)
        val result = parser.parse(tokenStream)

        assertTrue(result is ParserSyntaxError)
    }

    @Test
    fun `should return error for invalid token sequence`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                // Falta identificador
                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(tokenStream)
        val result = parser.parse(tokenStream)

        assertTrue(result is ParserSyntaxError)
        assertEquals(result.getError(), "Se esperaba un identificador después de 'let'")
    }

    @Test
    fun `should return error for missing closing parenthesis in grouping`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "3", Position()),
                // Falta el paréntesis de cierre
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(tokenStream)
        val result = parser.parse(tokenStream)

        assertTrue(result is ParserSyntaxError)
    }

    @Test
    fun `should return error for missing closing parenthesis in function call`() {
        val tokens =
            listOf(
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.NUMBER, "42", Position()),
                // Falta el paréntesis de cierre
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(tokenStream)
        val result = parser.parse(tokenStream)

        assertTrue(result is ParserSyntaxError)
    }

    @Test
    fun `should return error for incomplete binary expression`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                // Falta el operando derecho
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(tokenStream)
        val result = parser.parse(tokenStream)

        assertTrue(result is ParserSyntaxError)
    }

    @Test
    fun `should return error for unknown token`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPERATOR, "@", Position()),
                // Operador inválido
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(tokenStream)
        val result = parser.parse(tokenStream)

        assertTrue(result is ParserSyntaxError)
    }

    @Test
    fun `should return error for missing function name`() {
        val tokens =
            listOf(
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                // Falta el nombre de la función
                Token(TokenType.NUMBER, "42", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(tokenStream)
        val result = parser.parse(tokenStream)

        assertTrue(result is ParserSyntaxError)
    }
}
