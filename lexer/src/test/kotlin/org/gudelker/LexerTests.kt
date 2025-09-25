package org.gudelker

import org.gudelker.lexer.LexerFactory
import org.gudelker.resultlexer.LexerSyntaxError
import org.gudelker.resultlexer.ValidTokens
import org.gudelker.sourcereader.FileSourceReader
import org.gudelker.sourcereader.StringSourceReader
import org.gudelker.token.TokenType
import org.gudelker.utilities.Version
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class LexerTests {
    private val lexerV1 = LexerFactory.createLexer(Version.V1)
    private val lexerV2 = LexerFactory.createLexer(Version.V2)

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `test token rules`() {
        val reader = FileSourceReader("src/test/lexer.txt")
        when (val tokens = lexerV1.lex(reader)) {
            is ValidTokens ->
                for (token in tokens.getList()) println(token)
            is LexerSyntaxError ->
                println(tokens.getError())
        }
    }

    @Test
    fun `test token rules with if`() {
        val reader = FileSourceReader("src/test/lexer2.txt")
        when (val tokens = lexerV1.lex(reader)) {
            is ValidTokens ->
                for (token in tokens.getList()) println(token)
            is LexerSyntaxError ->
                println(tokens.getError())
        }
    }

    @Test
    fun `test token rules with if v2`() {
        val reader = FileSourceReader("src/test/lexer2.txt")
        when (val tokens = lexerV2.lex(reader)) {
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

    @Test
    fun `test lexer con declaraciones de variables de diferentes tipos`() {
        val code = """
        let num: number = 10;
        let str: string = "hola mundo";
        let flag: boolean = true;
    """
        val reader = StringSourceReader(code)
        val result = lexerV2.lex(reader)

        assert(result is ValidTokens)
        if (result is ValidTokens) {
            val tokens = result.getList()
            assert(tokens.isNotEmpty())
            assert(tokens.count { it.getType() == TokenType.TYPE } == 3)
            assert(tokens.any { it.getValue() == "number" })
            assert(tokens.any { it.getValue() == "string" })
            assert(tokens.any { it.getValue() == "boolean" })
        }
    }

    @Test
    fun `test lexer con expresiones aritméticas`() {
        val code = "let resultado = 5 + 10 * 2 - 8 / 4.0;"
        val reader = StringSourceReader(code)
        val result = lexerV2.lex(reader)

        assert(result is ValidTokens)
        if (result is ValidTokens) {
            val tokens = result.getList()
            val operadores = tokens.filter { it.getType() == TokenType.OPERATOR }
            assert(operadores.count() == 4)
            assert(operadores.any { it.getValue() == "+" })
            assert(operadores.any { it.getValue() == "*" })
            assert(operadores.any { it.getValue() == "-" })
            assert(operadores.any { it.getValue() == "/" })
        }
    }

    @Test
    fun `test lexer con estructura condicional if-else`() {
        val code = """
        if (x > 10) {
            println("Mayor que 10");
        } else {
            println("Menor o igual que 10");
        }
    """
        val reader = StringSourceReader(code)
        val result = lexerV2.lex(reader)

        assert(result is ValidTokens)
        if (result is ValidTokens) {
            val tokens = result.getList()
            assert(tokens.any { it.getType() == TokenType.IF_KEYWORD })
            assert(tokens.any { it.getType() == TokenType.ELSE_KEYWORD })
            assert(tokens.any { it.getType() == TokenType.COMPARATOR })
        }
    }
}
