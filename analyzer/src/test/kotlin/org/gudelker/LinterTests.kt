package org.gudelker

import org.gudelker.analyzers.BinaryExpressionAnalyzer
import org.gudelker.analyzers.CallableAnalyzer
import org.gudelker.analyzers.GroupingExpressionAnalyzer
import org.gudelker.analyzers.LiteralNumberAnalyzer
import org.gudelker.analyzers.UnaryExpressionAnalyzer
import org.gudelker.analyzers.VariableDeclarationAnalyzer
import org.gudelker.operator.AdditionOperator
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
            )
        val loader = JsonLinterConfigLoaderToMap("src/main/kotlin/org/gudelker/linterconfig.json")
        config = loader.loadConfig()
        linter = DefaultLinter(analyzers)
    }

    @Test
    fun `valid camelCase identifier passes`() {
        val stmt = VariableDeclaration("myVar", "number", LiteralNumber(1))
        val result = linter.lint(StatementStream(listOf(stmt)), config)
        assertEquals(result.results, emptyList<LintViolation>())
    }

    @Test
    fun `snake_case variable and println with expression argument yields only one violation`() {
        val stmts =
            listOf(
                VariableDeclaration("my_var", "number", LiteralNumber(1)),
                Callable("println", Binary(LiteralNumber(1), AdditionOperator("+"), LiteralNumber(2))),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertEquals(2, result.results.size)
        assertTrue(result.results[0] is LintViolation)
    }

    @Test
    fun `valid snake_case identifier fails when config is camelCase`() {
        val stmt = VariableDeclaration("my_var", "number", LiteralNumber(1))
        val result = linter.lint(StatementStream(listOf(stmt)), config)
        assertTrue(result.results.any { it is LintViolation })
    }

    @Test
    fun `println with literal argument passes when restrictPrintlnExpressions is true`() {
        val stmt = Callable("println", LiteralNumber(42))
        val result = linter.lint(StatementStream(listOf(stmt)), config)
        assertEquals(emptyList<LintViolation>(), result.results)
    }

    @Test
    fun `multiple variable declarations with mixed formats yield correct violations`() {
        val stmts =
            listOf(
                VariableDeclaration("myVar", "number", LiteralNumber(1)),
                VariableDeclaration("my_var", "number", LiteralNumber(2)),
                VariableDeclaration("anotherVar", "number", LiteralNumber(3)),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertEquals(1, result.results.size)
        assertTrue(result.results[0] is LintViolation)
    }

    @Test
    fun `empty statement stream passes with no violations`() {
        val stmts = emptyList<Statement>()
        val result = linter.lint(StatementStream(stmts), config)
        assertEquals(emptyList<LintViolation>(), result.results)
    }
}
