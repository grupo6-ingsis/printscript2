package org.gudelker.analyzers

import org.gudelker.expressions.LiteralBoolean
import org.gudelker.linter.Linter
import org.gudelker.linter.LinterConfig
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.rulelinter.RuleLinter
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnalyzersTest {
    private lateinit var mockLinter: Linter
    private lateinit var mockRuleTrue: RuleLinter
    private lateinit var mockRuleFalse: RuleLinter

    @BeforeEach
    fun setup() {
        mockLinter =
            object : Linter {
                override fun lint(
                    statementStream: org.gudelker.stmtposition.StatementStream,
                    rules: Map<String, LinterConfig>,
                ) = throw UnsupportedOperationException("No implementado para prueba")

                override fun lintNode(
                    statement: Statement,
                    ruleMap: Map<String, LinterConfig>,
                    results: List<LinterResult>,
                ): List<LinterResult> {
                    return results + ValidLint("Mock lintNode pasó")
                }
            }

        mockRuleTrue =
            object : RuleLinter {
                override fun matches(config: Map<String, LinterConfig>): Boolean = true

                override fun validate(statement: Statement): LinterResult {
                    return ValidLint("Regla siempre válida")
                }
            }

        mockRuleFalse =
            object : RuleLinter {
                override fun matches(config: Map<String, LinterConfig>): Boolean = false

                override fun validate(statement: Statement): LinterResult {
                    return LintViolation("Esta violación nunca debería verse", StatementPosition(1, 1, 1, 1))
                }
            }
    }

    @Test
    fun `LiteralBooleanLintAnalyzer maneja solo LiteralBoolean`() {
        val analyzer = LiteralBooleanLintAnalyzer(listOf(mockRuleTrue))

        val booleanStmt = LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 1, 1, 5)))
        val otherStmt = object : Statement {}

        assertTrue(analyzer.canHandle(booleanStmt))
        assertFalse(analyzer.canHandle(otherStmt))
    }

    @Test
    fun `LiteralBooleanLintAnalyzer aplica reglas que coinciden`() {
        val analyzer = LiteralBooleanLintAnalyzer(listOf(mockRuleTrue))
        val booleanStmt = LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 1, 1, 5)))
        val initialResults = emptyList<LinterResult>()
        val config = mapOf("test" to LinterConfig("camelCase", true, true))

        val results = analyzer.lint(booleanStmt, config, mockLinter, initialResults)

        assertEquals(1, results.size)
        assertTrue(results[0] is ValidLint)
        assertEquals("Regla siempre válida", (results[0] as ValidLint).message)
    }

    @Test
    fun `LiteralBooleanLintAnalyzer ignora reglas que no coinciden`() {
        val analyzer = LiteralBooleanLintAnalyzer(listOf(mockRuleFalse))
        val booleanStmt = LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 1, 1, 5)))
        val initialResults = emptyList<LinterResult>()
        val config = mapOf("test" to LinterConfig("camelCase", true, true))

        val results = analyzer.lint(booleanStmt, config, mockLinter, initialResults)

        assertTrue(results.isEmpty())
    }

    @Test
    fun `LiteralBooleanLintAnalyzer devuelve resultados originales cuando el statement no es compatible`() {
        val analyzer = LiteralBooleanLintAnalyzer(listOf(mockRuleTrue))
        val initialResult = ValidLint("Resultado inicial")
        val initialResults = listOf<LinterResult>(initialResult)
        val otherStmt = object : Statement {}
        val config = mapOf("test" to LinterConfig("camelCase", true, true))

        val results = analyzer.lint(otherStmt, config, mockLinter, initialResults)

        assertEquals(1, results.size)
        assertTrue(results[0] is ValidLint)
        assertEquals("Resultado inicial", (results[0] as ValidLint).message)
    }

    @Test
    fun `LiteralBooleanLintAnalyzer aplica múltiples reglas correctamente`() {
        // Regla que siempre genera una violación
        val violationRule =
            object : RuleLinter {
                override fun matches(config: Map<String, LinterConfig>): Boolean = true

                override fun validate(statement: Statement): LinterResult {
                    return LintViolation("Violación de prueba", StatementPosition(1, 1, 1, 1))
                }
            }

        val analyzer = LiteralBooleanLintAnalyzer(listOf(mockRuleTrue, violationRule))
        val booleanStmt = LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 1, 1, 5)))
        val initialResults = emptyList<LinterResult>()
        val config = mapOf("test" to LinterConfig("camelCase", true, true))

        val results = analyzer.lint(booleanStmt, config, mockLinter, initialResults)

        assertEquals(2, results.size)
        assertTrue(results[0] is ValidLint)
        assertTrue(results[1] is LintViolation)
        assertEquals("Violación de prueba", (results[1] as LintViolation).message)
    }
}
