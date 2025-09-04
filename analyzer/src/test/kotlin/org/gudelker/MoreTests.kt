package org.gudelker
import org.gudelker.analyzers.BinaryExpressionLintAnalyzer
import org.gudelker.analyzers.CallableLintAnalyzer
import org.gudelker.analyzers.GroupingExpressionLintAnalyzer
import org.gudelker.analyzers.LiteralNumberLintAnalyzer
import org.gudelker.analyzers.UnaryExpressionLintAnalyzer
import org.gudelker.analyzers.VariableDeclarationLintAnalyzer
import org.gudelker.analyzers.VariableReassginationLintAnalyzer
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.rulelinter.CamelCaseRule
import org.gudelker.rulelinter.RestrictPrintLnExpressions
import org.gudelker.rulelinter.SnakeCaseRule
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.collections.mapOf

class MoreTests {
    private lateinit var linter: Linter

    private fun createLinter(): DefaultLinter {
        val analyzers =
            listOf(
                VariableDeclarationLintAnalyzer(listOf(CamelCaseRule(), SnakeCaseRule())),
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
            )
        return DefaultLinter(analyzers)
    }

    @BeforeEach
    fun setup() {
        linter = createLinter()
    }

    @Test
    fun `test camelCase identifier format - valid`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("myVariable", StatementPosition(1, 5, 1, 9)),
                "Number",
                LiteralNumber(
                    ComboValuePosition(
                        3,
                        StatementPosition(1, 1, 1, 1),
                    ),
                ),
            )
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
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                        AdditionOperator(),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                    ),
                ),
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
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                        AdditionOperator(),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                    ),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                ),
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
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                        AdditionOperator(),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                    ),
                ),
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
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                        AdditionOperator(),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                    ),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                    "number",
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                ),
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
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("my_var", StatementPosition(1, 5, 1, 9)),
                    "number",
                    LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                        AdditionOperator(),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                    ),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Grouping(
                        "(",
                        Binary(
                            LiteralNumber(ComboValuePosition(1, StatementPosition(1, 1, 1, 1))),
                            AdditionOperator(),
                            LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                        ),
                        ")",
                    ),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    Unary(LiteralNumber(ComboValuePosition(3, StatementPosition(1, 1, 1, 1))), MinusOperator()),
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
//        val statement = VariableDeclaration("my_variable", "Number", LiteralNumber(ComboValuePosition(3, PositionStatement(1,1,1,1))
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
//        val expression = Binary(LiteralNumber(5), AdditionOperator(), LiteralNumber(ComboValuePosition(3, PositionStatement(1,1,1,1))
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
//        val expression = Binary(LiteralNumber(5), AdditionOperator(), LiteralNumber(ComboValuePosition(3, PositionStatement(1,1,1,1))
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
//        val expression = Binary(LiteralNumber(ComboValuePosition(1, StatementPosition(1,1,1,10), MultiplyOperator(), LiteralNumber(5))
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
//        val left = Binary(LiteralNumber(ComboValuePosition(1, StatementPosition(1,1,1,10), AdditionOperator(), LiteralNumber(5))
//        val right = Binary(LiteralNumber(8), DivisionOperator(), LiteralNumber(ComboValuePosition(2, StatementPosition(1,1,1,1))
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
//            VariableDeclaration("validName", "Int", LiteralNumber(ComboValuePosition(1, StatementPosition(1,1,1,10)),
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
//            VariableDeclaration("age", "Int", LiteralNumber(ComboValuePosition(2, StatementPosition(1,1,1,15)),
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
//            VariableDeclaration("valid_snake_case", "Int", LiteralNumber(ComboValuePosition(1, StatementPosition(1,1,1,1)),
//            VariableDeclaration("invalidCamelCase", "String", LiteralString("test")),
//            Callable("println", Binary(LiteralNumber(ComboValuePosition(1, StatementPosition(1,1,1,1), AdditionOperator(), LiteralNumber(ComboValuePosition(2, StatementPosition(1,1,1,1)))
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
//            Binary(LiteralNumber(ComboValuePosition(1, StatementPosition(1,1,1,1), AdditionOperator(), LiteralNumber(ComboValuePosition(2, StatementPosition(1,1,1,1)),
//            MultiplyOperator(),
//            LiteralNumber(ComboValuePosition(3, PositionStatement(1,1,1,1)
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
