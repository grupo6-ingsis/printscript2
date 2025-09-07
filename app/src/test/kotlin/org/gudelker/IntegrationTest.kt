package org.gudelker

import org.gudelker.parser.DefaultParserFactory
import org.gudelker.result.CompoundResult
import org.gudelker.result.LexerSyntaxError
import org.gudelker.result.LintViolation
import org.gudelker.result.Valid
import org.gudelker.result.ValidTokens
import org.gudelker.rules.FormatterRule
import org.gudelker.sourcereader.StringSourceReader
import org.gudelker.tokenstream.TokenStream
import org.gudelker.utilities.Version
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

    @Test
    fun `should handle number and string`() {
        val code =
            """
            let x = 6;
            let y = "hola";
            let pepe = x + y;
            println(pepe);
            """.trimIndent()

        val result = processCode(code)

        assertEquals(4, result.size)
        assertEquals(Unit, result[0]) // let x = 20
        assertEquals(Unit, result[1]) // let y = 4
        assertEquals(Unit, result[2]) // 20 / 4
    }

    private fun processCode(code: String): List<Any?> {
        // 1. Lexical Analysis
        val lexer = LexerFactory.createLexer(Version.V1)
        val sourceReader = StringSourceReader(code)
        val tokenResult = lexer.lex(sourceReader)

        when (tokenResult) {
            is LexerSyntaxError ->
                throw RuntimeException("Lexer error: $tokenResult")

            is ValidTokens -> {
                val tokens = tokenResult.getList()

                // 2. Syntax Analysis
                val tokenStream = TokenStream(tokens)
                val parser = DefaultParserFactory.createParser(Version.V1)
                val parseResult = parser.parse(tokenStream)

                if (parseResult !is Valid) {
                    throw RuntimeException("Parser error: $parseResult")
                }

                val statements = parseResult.getStatements()

                // 3. Interpretation
                val interpreter = InterpreterFactory.createInterpreter(Version.V1)
                return interpreter.interpret(statements)
            }
        }
    }

    @Test
    fun `should process simple if-else statement`() {
        val code =
            """
            let x = 4;
            if (x > 5) {
                println("Greater than 5");
            } else {
                println("Less or equal to 5");
            }
            """.trimIndent()

        val result = processCodeV2(code)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0])
        assertEquals(Unit, result[1])
    }

    @Test
    fun `should process if statement without else`() {
        val code =
            """
            let y = 3;
            if (y < 5) {
                let result = y * 2;
                println(result);
            }
            """.trimIndent()

        val result = processCodeV2(code)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0]) // let y = 3
        assertEquals(Unit, result[1]) // conditional with multiple statements
    }

    @Test
    fun `should not process const reassignment`() {
        val code =
            """
            const y = 5;
            let x = 10;
            x = 20 + y;
            println(x);
            """.trimIndent()

        val result = processCodeV2(code)

        assertEquals(4, result.size)
        assertEquals(Unit, result[0]) // declaración inicial
        assertEquals(Unit, result[1]) // reasignación
        assertEquals(Unit, result[2]) // valor final
    }

    @Test
    fun `should process with boolean statement without else`() {
        val code =
            """
            let y = true;
            if (y) {
                println("Y is true");
            }
            """.trimIndent()

        val result = processCodeV2(code)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0]) // let y = 3
        assertEquals(Unit, result[1]) // conditional with multiple statements
    }

    private fun processCodeV2(code: String): List<Any?> {
        // 1. Lexical Analysis
        val lexer = LexerFactory.createLexer(Version.V2)
        val sourceReader = StringSourceReader(code)
        val tokenResult = lexer.lex(sourceReader)

        when (tokenResult) {
            is LexerSyntaxError ->
                throw RuntimeException("Lexer error: $tokenResult")

            is ValidTokens -> {
                val tokens = tokenResult.getList()

                // 2. Syntax Analysis
                val tokenStream = TokenStream(tokens)
                val parser = DefaultParserFactory.createParser(Version.V2)
                val parseResult = parser.parse(tokenStream)

                if (parseResult !is Valid) {
                    throw RuntimeException("Parser error: $parseResult")
                }

                val statements = parseResult.getStatements()

                // 3. Interpretation
                val interpreter = InterpreterFactory.createInterpreter(Version.V2)
                return interpreter.interpret(statements)
            }
        }
    }

    @Test
    fun `should return formatted code applying rules`() {
        val code =
            """
            let y:boolean=true;
            if (y){
            println("Y is true");
            }
            """.trimIndent()

        val result = formatCodeV2(code)

        val expectedCode =
            """
            let y :  boolean   =   true;
            if (y) {

                println("Y is true");
            }
            """.trimIndent()

        println(expectedCode.replace("\n", "\\n\n"))
        println(result.replace("\n", "\\n\n"))
        assertEquals(expectedCode, result)
    }

    @Test
    fun `should lint code with camelCase and snake_case identifiers`() {
        val code =
            """
            const myConst = true;
            let another_var = 42;
            println(myConst);
            """.trimIndent()

        val result = lintCodeV2(code)
        // myConst (camelCase) and another_var (snake_case) with camelCase rule: one violation
        assert(result.results.any { it is LintViolation })
        assertEquals(1, result.results.count { it is LintViolation })
    }

    @Test
    fun `should lint code with multiple violations`() {
        val code =
            """
            const bad_var = false;
            let another_var = 7;
            println(bad_var);
            println(another_var);
            """.trimIndent()

        val result = lintCodeV2(code)
        // bad_var and another_var (snake_case) with camelCase rule: two violations
        assert(result.results.any { it is LintViolation })
        assertEquals(2, result.results.count { it is LintViolation })
    }

    // File: app/src/test/kotlin/org/gudelker/IntegrationTest.kt

    @Test
    fun `should lint println allowing only literals in V2`() {
        val code =
            """
            println("hello");
            println(42);
            println(true);
            println(1 + 2);
            let x = 5;
            println(x);
            """.trimIndent()

        val result = lintCodeV2(code)
        assert(result.results.any { it is LintViolation })
        assertEquals(1, result.results.count { it is LintViolation })
    }

    private fun formatCodeV2(code: String): String {
        // 1. Lexical Analysis
        val lexer = LexerFactory.createLexer(Version.V2)
        val sourceReader = StringSourceReader(code)
        val tokenResult = lexer.lex(sourceReader)

        when (tokenResult) {
            is LexerSyntaxError ->
                throw RuntimeException("Lexer error: $tokenResult")

            is ValidTokens -> {
                val tokens = tokenResult.getList()

                // 2. Syntax Analysis
                val tokenStream = TokenStream(tokens)
                val parser = DefaultParserFactory.createParser(Version.V2)
                val parseResult = parser.parse(tokenStream)

                if (parseResult !is Valid) {
                    throw RuntimeException("Parser error: $parseResult")
                }

                val statements = parseResult.getStatements()
                val formatter = DefaultFormatterFactory.createFormatter(Version.V2)

                val rules =
                    mapOf(
                        "beforeDeclaration" to FormatterRule(on = true, quantity = 1),
                        "afterDeclaration" to FormatterRule(on = true, quantity = 2),
                        "assignDeclaration" to FormatterRule(on = true, quantity = 3),
                        "ifIndentation" to FormatterRule(on = true, quantity = 4),
                        "println" to FormatterRule(on = true, quantity = 1),
                    )

                return statements.joinToString("\n") { formatter.format(it, rules) }
            }
        }
    }

    private fun lintCodeV2(code: String): CompoundResult {
        val lexer = LexerFactory.createLexer(Version.V2)
        val sourceReader = StringSourceReader(code)
        val tokenResult = lexer.lex(sourceReader)

        when (tokenResult) {
            is LexerSyntaxError -> throw RuntimeException("Lexer error: $tokenResult")
            is ValidTokens -> {
                val tokens = tokenResult.getList()
                val tokenStream = TokenStream(tokens)
                val parser = DefaultParserFactory.createParser(Version.V2)
                val parseResult = parser.parse(tokenStream)

                if (parseResult !is Valid) {
                    throw RuntimeException("Parser error: $parseResult")
                }

                val statements = parseResult.getStatements()
                val linter = DefaultLinterFactory.createLinter(Version.V2)
                val rules =
                    mapOf(
                        "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                        "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                    )
                return linter.lint(StatementStream(statements), rules)
            }
        }
    }
}
