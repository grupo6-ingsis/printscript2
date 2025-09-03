package org.gudelker

import org.gudelker.result.LexerSyntaxError
import org.gudelker.result.ValidTokens
import org.gudelker.sourcereader.FileSourceReader
import org.gudelker.sourcereader.StringSourceReader
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class LexerTests {
    val lexerV1 = LexerFactory.createLexer(Version.V1)
    val lexerV2 = LexerFactory.createLexer(Version.V2)

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `test token rules`() {
        // Usar ruta relativa desde la raíz del proyecto
        // Ahora no hay que cambiarlo constantemente
        val reader = FileSourceReader("src/test/lexer.txt")
        val tokens = lexerV1.lex(reader)
        when (tokens) {
            is ValidTokens ->
                for (token in tokens.getList()) println(token)
            is LexerSyntaxError ->
                println(tokens.getError())
        }
    }

    @Test
    fun `test token rules with if`() {
        // Se buguea no más el parser me va a tirar el error
        val reader = FileSourceReader("src/test/lexer2.txt")
        val tokens = lexerV1.lex(reader)
        when (tokens) {
            is ValidTokens ->
                for (token in tokens.getList()) println(token)
            is LexerSyntaxError ->
                println(tokens.getError())
        }
    }

    @Test
    fun `test token rules with if v2`() {
        // Se buguea no más el parser me va a tirar el error
        val reader = FileSourceReader("src/test/lexer2.txt")
        val tokens = lexerV2.lex(reader)
        when (tokens) {
            is ValidTokens ->
                for (token in tokens.getList()) println(token)
            is LexerSyntaxError ->
                println(tokens.getError())
        }
    }

    @Test
    fun `test lexer with string source reader valid code`() {
        val validCode = "let x: number = 42;"
        val reader = StringSourceReader(validCode)
        val result = lexerV1.lex(reader)

        assert(result is ValidTokens)
        assert(result.isValid())
        if (result is ValidTokens) {
            assert(result.getList().isNotEmpty())
        }
    }

    @Test
    fun `test lexer with empty string`() {
        val emptyCode = ""
        val reader = StringSourceReader(emptyCode)
        val result = lexerV1.lex(reader)

        assert(result is ValidTokens)
        assert(result.isValid())
    }
}
