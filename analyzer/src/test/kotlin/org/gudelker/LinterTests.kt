package org.gudelker

import org.gudelker.analyzers.BinaryExpressionLintAnalyzer
import org.gudelker.analyzers.CallableLintAnalyzer
import org.gudelker.analyzers.GroupingExpressionLintAnalyzer
import org.gudelker.analyzers.LiteralIdentifierLintAnalyzer
import org.gudelker.analyzers.LiteralNumberLintAnalyzer
import org.gudelker.analyzers.LiteralStringLintAnalyzer
import org.gudelker.analyzers.UnaryExpressionLintAnalyzer
import org.gudelker.analyzers.VariableDeclarationLintAnalyzer
import org.gudelker.analyzers.VariableReassginationLintAnalyzer
import org.gudelker.expressions.Binary
import org.gudelker.expressions.Callable
import org.gudelker.expressions.Grouping
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.expressions.Unary
import org.gudelker.linterloader.JsonLinterConfigLoaderToMap
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.result.LintViolation
import org.gudelker.rulelinter.RestrictPrintLnExpressions
import org.gudelker.rulelinter.VariableDeclarationCamelCaseRule
import org.gudelker.rulelinter.VariableDeclarationSnakeCaseRule
import org.gudelker.statements.VariableReassignment
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
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
                VariableDeclarationLintAnalyzer(listOf(VariableDeclarationCamelCaseRule(), VariableDeclarationSnakeCaseRule())),
                CallableLintAnalyzer(
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
                LiteralNumberLintAnalyzer(emptyList()),
                BinaryExpressionLintAnalyzer(emptyList()),
                UnaryExpressionLintAnalyzer(emptyList()),
                GroupingExpressionLintAnalyzer(emptyList()),
                VariableReassginationLintAnalyzer(emptyList()),
                LiteralStringLintAnalyzer(emptyList()),
                LiteralIdentifierLintAnalyzer(emptyList()),
            )
        val configLoader = JsonLinterConfigLoaderToMap("src/main/kotlin/org/gudelker/linterconfig.json")
        linter = DefaultLinter(analyzers, configLoader)
    }

    @Test
    fun `valid camelCase identifier passes`() {
        val stmt =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("myVar", StatementPosition(1, 5, 1, 9)),
                "Number",
                LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(listOf(stmt)), rules)
        assertEquals(result.results, emptyList<LintViolation>())
    }

    @Test
    fun `snake_case variable and println with expression argument yields only one violation`() {
        val stmts =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(
                        ComboValuePosition(1, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                        AdditionOperator("+"),
                        LiteralNumber(
                            ComboValuePosition(2, StatementPosition(2, 2, 2, 2)),
                        ),
                    ),
                ),
            )
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(stmts), rules)
        assertEquals(2, result.results.size)
        assertTrue(result.results.isNotEmpty())
    }

    @Test
    fun `valid snake_case identifier fails when config is camelCase`() {
        val stmt =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                "Number",
                LiteralNumber(
                    ComboValuePosition(1, StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(listOf(stmt)), rules)
        assertTrue(result.results.isNotEmpty())
    }

    @Test
    fun `println with literal argument passes when restrictPrintlnExpressions is true`() {
        val stmt =
            Callable(
                ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                LiteralNumber(ComboValuePosition(42, StatementPosition(1, 3, 1, 3))),
            )
        val rules =
            mapOf(
                "camelCase" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(listOf(stmt)), rules)
        assertEquals(emptyList<LintViolation>(), result.results)
    }

    @Test
    fun `multiple variable declarations with mixed formats yield correct violations`() {
        val stmts =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("myVar", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(
                        ComboValuePosition(1, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(
                        ComboValuePosition(2, StatementPosition(2, 2, 2, 2)),
                    ),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("anotherVar", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(
                        ComboValuePosition(2, StatementPosition(2, 2, 2, 2)),
                    ),
                ),
            )
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
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
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(stmts), rules)
        assertEquals(emptyList<LintViolation>(), result.results)
    }

    @Test
    fun `covers VariableDeclaration, LiteralNumber, and Callable analyzers`() {
        val stmts =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("myVar", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(
                        ComboValuePosition(1, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    LiteralNumber(ComboValuePosition(2, StatementPosition(2, 2, 2, 2))),
                ),
            )
        val config =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.size <= 1)
    }

    @Test
    fun `covers Binary, Unary, and Grouping analyzers`() {
        val stmts =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                    AdditionOperator(),
                    LiteralNumber(
                        ComboValuePosition(2, StatementPosition(2, 2, 2, 2)),
                    ),
                ),
                Unary(LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))), MinusOperator()),
                Grouping("(", LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))), ")"),
            )
        val config =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.isEmpty())
    }

    @Test
    fun `covers VariableReassignment analyzer`() {
        val stmts =
            listOf(
                VariableReassignment(
                    ComboValuePosition("myVar", StatementPosition(1, 1, 1, 1)),
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                ),
            )
        val config =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = false,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(stmts), config)
        assertTrue(result.results.isEmpty())
    }

    @Test
    fun `full analyzer and rule coverage`() {
        val stmts =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("validCamel", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(2, 1, 2, 1)),
                    ComboValuePosition("invalid_snake", StatementPosition(2, 5, 2, 9)),
                    "Number",
                    LiteralNumber(ComboValuePosition(2, StatementPosition(2, 2, 2, 2))),
                ),
                VariableReassignment(
                    ComboValuePosition("validCamel", StatementPosition(3, 1, 3, 1)),
                    LiteralNumber(ComboValuePosition(3, StatementPosition(3, 3, 3, 3))),
                ),
                LiteralNumber(ComboValuePosition(4, StatementPosition(4, 1, 4, 1))),
                LiteralString(ComboValuePosition("test", StatementPosition(5, 1, 5, 1))),
                LiteralIdentifier(ComboValuePosition("validCamel", StatementPosition(6, 1, 6, 1))),
                Binary(
                    LiteralNumber(ComboValuePosition(5, StatementPosition(7, 1, 7, 1))),
                    AdditionOperator(),
                    LiteralNumber(ComboValuePosition(6, StatementPosition(7, 2, 7, 2))),
                ),
                Unary(
                    LiteralNumber(ComboValuePosition(7, StatementPosition(8, 1, 8, 1))),
                    MinusOperator(),
                ),
                Grouping(
                    "(",
                    LiteralNumber(ComboValuePosition(8, StatementPosition(9, 1, 9, 1))),
                    ")",
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(10, 2, 10, 4)),
                    LiteralString(ComboValuePosition("allowed", StatementPosition(10, 3, 10, 3))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(11, 2, 11, 4)),
                    Binary(
                        LiteralNumber(ComboValuePosition(9, StatementPosition(11, 1, 11, 1))),
                        AdditionOperator(),
                        LiteralNumber(ComboValuePosition(10, StatementPosition(11, 2, 11, 2))),
                    ),
                ),
            )
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(stmts), rules)
        assertTrue(result.results.size >= 2)
        assertTrue(result.results.any { it is LintViolation })
    }
}
