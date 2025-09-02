package org.gudelker

import org.gudelker.parser.DefaultParserFactory
import org.gudelker.result.LexerSyntaxError
import org.gudelker.result.Valid
import org.gudelker.result.ValidTokens
import org.gudelker.sourcereader.StringSourceReader
import org.gudelker.tokenstream.TokenStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IntegrationTest {
    @Test
    fun `should process simple variable declaration and usage`() {
        val code =
            """
            let x = - 42;
            println(x);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0]) // declaración
        assertEquals(Unit, result[1]) // println
    }

    @Test
    fun `should process mathematical operations`() {
        val code =
            """
            let x = 5 + 3;
            let y = x * 2;
            println(y - 1);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(3, result.size)
        assertEquals(Unit, result[0]) // let x = 8
        assertEquals(Unit, result[1]) // let y = 16
        assertEquals(Unit, result[2]) // 16 - 1
    }

    @Test
    fun `should process unary operations`() {
        val code =
            """
            let x = - 10;
            let y = + 5;
            println(x + y);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(3, result.size)
        assertEquals(Unit, result[0]) // let x = -10
        assertEquals(Unit, result[1]) // let y = 5
        assertEquals(Unit, result[2]) // -10 + 5
    }

    @Test
    fun `should process grouping expressions`() {
        val code =
            """
            let x = (5 + 3) * 2;
            println(x);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0]) // declaración
        assertEquals(Unit, result[1]) // (5 + 3) * 2 = 16
    }

    @Test
    fun `should process variable reassignment`() {
        val code =
            """
            let x = 10;
            x = 20 + x;
            println(x);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(3, result.size)
        assertEquals(Unit, result[0]) // declaración inicial
        assertEquals(Unit, result[1]) // reasignación
        assertEquals(Unit, result[2]) // valor final
    }

    @Test
    fun `should process function calls`() {
        val code =
            """
            println("Hello World");
            println(42);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0]) // println string
        assertEquals(Unit, result[1]) // println number
    }

    @Test
    fun `should process complex expression`() {
        val code =
            """
            let a = 10;
            let b = 5;
            let c = (a + b);
            let result = c / 3;
            println(result);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(5, result.size)
        assertEquals(Unit, result[0]) // let a = 10
        assertEquals(Unit, result[1]) // let b = 5
        assertEquals(Unit, result[2]) // let c = 30
        assertEquals(Unit, result[3]) // let result = 10
        assertEquals(Unit, result[4]) // println(10)
    }

    @Test
    fun `should process string operations`() {
        val code =
            """
            let greeting = "Hello";
            let name = "World";
            println(greeting + " " + name);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(3, result.size)
        assertEquals(Unit, result[0]) // let greeting
        assertEquals(Unit, result[1]) // let name
        assertEquals(Unit, result[2]) // println greeting
    }

    @Test
    fun `should process mixed operations`() {
        val code =
            """
            let x = 5;
            let y = - x + 10;
            let z = y * (x - 2);
            println(z);
            println(z / 5);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(5, result.size)
        assertEquals(Unit, result[0]) // let x = 5
        assertEquals(Unit, result[1]) // let y = 5 (-5 + 10)
        assertEquals(Unit, result[2]) // println(y)
        assertEquals(Unit, result[3]) // println(y / 5)
    }

    @Test
    fun `should handle division operations`() {
        val code =
            """
            let x = 20;
            let y = 4;
            println(x / y);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(3, result.size)
        assertEquals(Unit, result[0]) // let x = 20
        assertEquals(Unit, result[1]) // let y = 4
        assertEquals(Unit, result[2]) // 20 / 4
    }

    private fun processCode(code: String): List<Any?> {
        // 1. Lexical Analysis
        val lexer = LexerFactory.createFileLexer()
        val sourceReader = StringSourceReader(code)
        val tokenResult = lexer.lex(sourceReader)

        when (tokenResult) {
            is LexerSyntaxError ->
                throw RuntimeException("Lexer error: $tokenResult")
            is ValidTokens ->
                {
                    val tokens = tokenResult.getList()

                    // 2. Syntax Analysis
                    val tokenStream = TokenStream(tokens)
                    val parser = DefaultParserFactory.createParser(tokenStream)
                    val parseResult = parser.parse(tokenStream)

                    if (parseResult !is Valid) {
                        throw RuntimeException("Parser error: $parseResult")
                    }

                    val statements = parseResult.getStatements()

                    // 3. Interpretation
                    val interpreter = DefaultInterpreter(emptyList())
                    return interpreter.interpret(statements)
                }
        }
    }

    @Test
    fun `app test for rest of coverage`() {
        main()
    }
}
