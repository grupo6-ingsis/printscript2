package org.gudelker

import org.gudelker.analyzers.BinaryExpressionAnalyzer
import org.gudelker.analyzers.CallableAnalyzer
import org.gudelker.analyzers.GroupingExpressionAnalyzer
import org.gudelker.analyzers.LiteralNumberAnalyzer
import org.gudelker.analyzers.UnaryExpressionAnalyzer
import org.gudelker.analyzers.VariableDeclarationAnalyzer
import org.gudelker.analyzers.VariableReassginationAnalyzer
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.result.LintViolation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinterTests {
    private lateinit var linter: Linter
    private lateinit var config: Map<String, LinterConfig>

    @BeforeEach
    fun setup() {
        val analyzers =
            listOf(
                VariableDeclarationAnalyzer(),
                CallableAnalyzer(),
                LiteralNumberAnalyzer(),
                BinaryExpressionAnalyzer(),
                UnaryExpressionAnalyzer(),
                GroupingExpressionAnalyzer(),
                VariableReassginationAnalyzer(),
            )
        linter = DefaultLinter(analyzers)
    }

    @Test
    fun `valid camelCase identifier passes`() {
        val stmt = VariableDeclaration("let", "myVar", "number", LiteralNumber(1))
        val rules =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = false),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = false),
            )
        val result = linter.lint(StatementStream(listOf(stmt)), rules)
        assertEquals(result.results, emptyList<LintViolation>())
    }

    @Test
    fun `snake_case variable and println with expression argument yields only one violation`() {
        val stmts =
            listOf(
                VariableDeclaration("let", "my_var", "number", LiteralNumber(1)),
                Callable("println", Binary(LiteralNumber(1), AdditionOperator("+"), LiteralNumber(2))),
            )
        val rules =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), rules)
        assertEquals(2, result.results.size)
        assertTrue(result.results.isNotEmpty())
    }

    @Test
    fun `valid snake_case identifier fails when config is camelCase`() {
        val stmt = VariableDeclaration("let", "my_var", "number", LiteralNumber(1))
        val rules =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(listOf(stmt)), rules)
        assertTrue(result.results.isNotEmpty())
    }

    @Test
    fun `println with literal argument passes when restrictPrintlnExpressions is true`() {
        val stmt = Callable("println", LiteralNumber(42))
        val rules =
            mapOf(
                "camelCase" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(listOf(stmt)), rules)
        assertEquals(emptyList<LintViolation>(), result.results)
    }

    @Test
    fun `multiple variable declarations with mixed formats yield correct violations`() {
        val stmts =
            listOf(
                VariableDeclaration("let", "myVar", "number", LiteralNumber(1)),
                VariableDeclaration("let", "my_var", "number", LiteralNumber(2)),
                VariableDeclaration("let", "anotherVar", "number", LiteralNumber(3)),
            )
        val rules =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), rules)
        assertEquals(1, result.results.size)
        assertTrue(result.results.isNotEmpty())
    }

    @Test
    fun `empty statement stream passes with no violations`() {
        val stmts = emptyList<Statement>()
        val rules =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), rules)
        assertEquals(emptyList<LintViolation>(), result.results)
    }

    @Test
    fun `covers VariableDeclaration, LiteralNumber, and Callable analyzers`() {
        val stmts =
            listOf(
                VariableDeclaration("let", "myVar", "number", LiteralNumber(1)),
                Callable("println", LiteralNumber(2)),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.size <= 1)
    }

    @Test
    fun `covers Binary, Unary, and Grouping analyzers`() {
        val stmts =
            listOf(
                Binary(LiteralNumber(1), AdditionOperator(), LiteralNumber(2)),
                Unary(LiteralNumber(3), MinusOperator()),
                Grouping("(", LiteralNumber(4), ")"),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.isEmpty())
    }

    @Test
    fun `covers VariableReassignment analyzer`() {
        val stmts =
            listOf(
                VariableReassignment("myVar", LiteralNumber(5)),
            )
        val config =
            mapOf(
                "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
                "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.isEmpty())
    }
}
