package org.gudelker

import org.example.org.gudelker.rulelinter.CamelCaseRule
import org.gudelker.analyzers.BinaryExpressionAnalyzer
import org.gudelker.analyzers.CallableAnalyzer
import org.gudelker.analyzers.GroupingExpressionAnalyzer
import org.gudelker.analyzers.LiteralNumberAnalyzer
import org.gudelker.analyzers.UnaryExpressionAnalyzer
import org.gudelker.analyzers.VariableDeclarationAnalyzer
import org.gudelker.analyzers.VariableReassginationAnalyzer
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.rulelinter.RestrictPrintLnExpressions
import org.gudelker.rulelinter.SnakeCaseRule
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.collections.mapOf

class MoreTests {
    private lateinit var linter: Linter

    private fun createLinter(): DefaultLinter {
        val analyzers =
            listOf(
                VariableDeclarationAnalyzer(listOf(CamelCaseRule(), SnakeCaseRule())),
                CallableAnalyzer(
                    listOf(
                        RestrictPrintLnExpressions(
                            listOf(
                                LiteralString::class,
                                LiteralNumber::class,
                                LiteralIdentifier::class,
                            ),
                        ),
                    ),
                ),
                LiteralNumberAnalyzer(emptyList()),
                BinaryExpressionAnalyzer(emptyList()),
                UnaryExpressionAnalyzer(emptyList()),
                GroupingExpressionAnalyzer(emptyList()),
                VariableReassginationAnalyzer(emptyList()),
            )
        return DefaultLinter(analyzers)
    }

    @BeforeEach
    fun setup() {
        linter = createLinter()
    }

    @Test
    fun `test camelCase identifier format - valid`() {
        val statement = VariableDeclaration("let", "myVariable", "Number", LiteralNumber(3))
        val list = listOf(statement)
        val config =
            mapOf(
                "rules" to
                    LinterConfig(
                        identifierFormat = "camelCase",
                        restrictPrintlnExpressions = false,
                    ),
            )

        val result = linter.lint(StatementStream(list), config)
        val resultList = result.results
        assertTrue(resultList.isEmpty())
    }

    @Test
    fun `snake_case variable and println with expression yields one violation with snake_case config`() {
        val stmts =
            listOf(
                VariableDeclaration("let", "my_var", "number", LiteralNumber(1)),
                Callable("println", Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2))),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "snake_case", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "snake_case", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.size == 1)
    }

    @Test
    fun `println with expression and snake_case variable yields one violation with snake_case config`() {
        val stmts =
            listOf(
                Callable("println", Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2))),
                VariableDeclaration("let", "my_var", "number", LiteralNumber(1)),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "snake_case", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "snake_case", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.size == 1)
    }

    @Test
    fun `snake_case variable and println with expression yields two violations with camelCase config`() {
        val stmts =
            listOf(
                VariableDeclaration("let", "my_var", "number", LiteralNumber(1)),
                Callable("println", Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2))),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.size == 2)
    }

    @Test
    fun `println with expression and snake_case variable yields two violations with camelCase config`() {
        val stmts =
            listOf(
                Callable("println", Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2))),
                VariableDeclaration("let", "my_var", "number", LiteralNumber(1)),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.size == 2)
    }

    @Test
    fun `all analyzers are exercised with a diverse statement list`() {
        val stmts =
            listOf(
                VariableDeclaration("let", "my_var", "number", LiteralNumber(1)),
                Callable("println", LiteralNumber(2)),
                Callable(
                    "println",
                    Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2)),
                ),
                Callable(
                    "println",
                    Grouping("(", Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2)), ")"),
                ),
                Callable(
                    "println",
                    Unary(LiteralNumber(3), MinusOperator()),
                ),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.size >= 1)
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
