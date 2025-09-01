package org.gudelker

import org.gudelker.analyzers.BinaryExpressionAnalyzer
import org.gudelker.analyzers.CallableAnalyzer
import org.gudelker.analyzers.GroupingExpressionAnalyzer
import org.gudelker.analyzers.LiteralNumberAnalyzer
import org.gudelker.analyzers.UnaryExpressionAnalyzer
import org.gudelker.analyzers.VariableDeclarationAnalyzer
import org.gudelker.result.ValidLint
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MoreTests {
    private lateinit var linter: Linter

    private fun createLinter(): DefaultLinter {
        val analyzers =
            listOf(
                VariableDeclarationAnalyzer(),
                CallableAnalyzer(),
                LiteralNumberAnalyzer(),
                BinaryExpressionAnalyzer(),
                UnaryExpressionAnalyzer(),
                GroupingExpressionAnalyzer(),
            )
        val loader = JsonLinterConfigLoaderToMap("src/main/kotlin/org/gudelker/linterconfig.json")
        return DefaultLinter(analyzers)
    }

    @BeforeEach
    fun setup() {
        linter = createLinter()
    }

    @Test
    fun `test camelCase identifier format - valid`() {
        val statement = VariableDeclaration("myVariable", "Number", LiteralNumber(3))
        val config =
            mapOf(
                "rules" to
                    LinterConfig(
                        identifierFormat = "camelCase",
                        restrictPrintlnExpressions = false,
                    ),
            )

        val result = linter.lintNode(statement, config)
        assertTrue(result is ValidLint)
    }
//
//    @Test
//    fun `test camelCase identifier format - invalid snake_case`() {
//        val statement = VariableDeclaration("my_variable", "Number", LiteralNumber(3))
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "snake_case",
//                restrictPrintlnExpressions = false
//            )
//        )
//
//        val result = linter.lint(StatementStream(listOf(statement)), config)
//        assertTrue(result.results.isNotEmpty())
//    }
//
//    @Test
//    fun `test snake_case identifier format - valid`() {
//        val statement = VariableDeclaration("my_variable", "String", LiteralString("test"))
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "snake_case",
//                restrictPrintlnExpressions = false
//            )
//        )
//
//        val result = linter.lintNode(statement, config)
//        assertTrue(result is ValidLint)
//    }
//
//    @Test
//    fun `test snake_case identifier format - invalid camelCase`() {
//        val statement = VariableDeclaration("myVariable", "String", LiteralString("test"))
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "snake_case",
//                restrictPrintlnExpressions = false
//            )
//        )
//
//        val result = linter.lintNode(statement, config)
//        assertTrue(result is LintViolation)
//    }
//
//    @Test
//    fun `test println with literal - allowed`() {
//        val statement = Callable("println", LiteralString("Hello World"))
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = true
//            )
//        )
//
//        val result = linter.lintNode(statement, config)
//        assertTrue(result is ValidLint)
//    }
//
//    @Test
//    fun `test println with expression - restricted`() {
//        val expression = Binary(LiteralNumber(5), AdditionOperator(), LiteralNumber(3))
//        val statement = Callable("println", expression)
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = true
//            )
//        )
//
//        val result = linter.lintNode(statement, config)
//        assertTrue(result is LintViolation)
//    }
//
//    @Test
//    fun `test println with expression - not restricted`() {
//        val expression = Binary(LiteralNumber(5), AdditionOperator(), LiteralNumber(3))
//        val statement = Callable("println", expression)
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = false
//            )
//        )
//
//        val result = linter.lintNode(statement, config)
//        assertTrue(result is ValidLint)
//    }
//
//    @Test
//    fun `test binary expression with valid operands`() {
//        val expression = Binary(LiteralNumber(10), MultiplyOperator(), LiteralNumber(5))
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = false
//            )
//        )
//
//        val result = linter.lintNode(expression, config)
//        assertTrue(result is ValidLint)
//    }
//
//    @Test
//    fun `test complex binary expression`() {
//        val left = Binary(LiteralNumber(10), AdditionOperator(), LiteralNumber(5))
//        val right = Binary(LiteralNumber(8), DivisionOperator(), LiteralNumber(2))
//        val expression = Binary(left, MinusOperator(), right)
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = false
//            )
//        )
//
//        val result = linter.lintNode(expression, config)
//        assertTrue(result is ValidLint)
//    }
//
//    @Test
//    fun `test statement stream with multiple statements`() {
//        val statements = listOf(
//            VariableDeclaration("validName", "Int", LiteralNumber(10)),
//            VariableDeclaration("invalid_name", "String", LiteralString("test")),
//            Callable("println", LiteralString("Hello"))
//        )
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = true
//            )
//        )
//
//        val result = linter.lint(StatementStream(statements), config)
//        assertEquals(1, result.results.size)
//        assertTrue(result.results.first() is LintViolation)
//    }
//
//    @Test
//    fun `test statement stream with all valid statements`() {
//        val statements = listOf(
//            VariableDeclaration("firstName", "String", LiteralString("John")),
//            VariableDeclaration("age", "Int", LiteralNumber(25)),
//            Callable("println", LiteralString("Valid output"))
//        )
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = true
//            )
//        )
//
//        val result = linter.lint(StatementStream(statements), config)
//        assertEquals(0, result.results.size)
//    }
//
//    @Test
//    fun `test statement stream with mixed violations`() {
//        val statements = listOf(
//            VariableDeclaration("valid_snake_case", "Int", LiteralNumber(1)),
//            VariableDeclaration("invalidCamelCase", "String", LiteralString("test")),
//            Callable("println", Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2)))
//        )
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "snake_case",
//                restrictPrintlnExpressions = true
//            )
//        )
//
//        val result = linter.lint(StatementStream(statements), config)
//        assertEquals(2, result.results.size)
//        assertTrue(result.results.all { it is LintViolation })
//    }
//
//    @Test
//    fun `test empty statement stream`() {
//        val statements = emptyList<Statement>()
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = true
//            )
//        )
//
//        val result = linter.lint(StatementStream(statements), config)
//        assertEquals(0, result.results.size)
//    }
//
//    @Test
//    fun `test variable declaration without type`() {
//        val statement = VariableDeclaration("myVar", null, LiteralNumber(42))
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = false
//            )
//        )
//
//        val result = linter.lintNode(statement, config)
//        assertTrue(result is ValidLint)
//    }
//
//    @Test
//    fun `test nested expressions in println`() {
//        val nestedExpression = Binary(
//            Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2)),
//            MultiplyOperator(),
//            LiteralNumber(3)
//        )
//        val statement = Callable("println", nestedExpression)
//        val config = mapOf(
//            "rules" to LinterConfig(
//                identifierFormat = "camelCase",
//                restrictPrintlnExpressions = true
//            )
//        )
//
//        val result = linter.lintNode(statement, config)
//        assertTrue(result is LintViolation)
//    }
}
