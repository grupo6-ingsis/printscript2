package org.gudelker
import org.gudelker.compare.operators.Equals
import org.gudelker.compare.operators.NotEquals
import org.gudelker.expressions.Binary
import org.gudelker.expressions.BooleanExpression
import org.gudelker.expressions.Callable
import org.gudelker.expressions.CallableCall
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.expressions.Grouping
import org.gudelker.expressions.LiteralBoolean
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.expressions.Unary
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.result.LintViolation
import org.gudelker.statements.VariableReassignment
import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.utilities.Version
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinterV2Test {
    private lateinit var linter: Linter
    private val rules =
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

    @BeforeEach
    fun setUp() {
        linter =
            DefaultLinterFactory.createLinter(
                Version.V2,
            )
    }

    @Test
    fun `test new statements with linter rules`() {
        val statements =
            listOf(
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    ComboValuePosition("myConst", StatementPosition(1, 6, 1, 12)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Boolean", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 13, 1, 17))),
                ),
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(2, 1, 2, 5)),
                    ComboValuePosition("my_const", StatementPosition(2, 6, 2, 13)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralNumber(ComboValuePosition(42, StatementPosition(2, 14, 2, 16))),
                ),
                ConditionalExpression(
                    ComboValuePosition("if", StatementPosition(3, 1, 3, 2)),
                    BooleanExpression(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(3, 3, 3, 4))),
                        Equals(),
                        LiteralNumber(ComboValuePosition(1, StatementPosition(3, 5, 3, 6))),
                    ),
                    listOf(
                        VariableDeclaration(
                            ComboValuePosition("let", StatementPosition(3, 7, 3, 10)),
                            ComboValuePosition("result", StatementPosition(3, 11, 3, 17)),
                            ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                            ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                            ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                            LiteralNumber(ComboValuePosition(100, StatementPosition(3, 18, 3, 21))),
                        ),
                    ),
                    ComboValuePosition("(", StatementPosition(4, 1, 4, 1)),
                    ComboValuePosition(")", StatementPosition(4, 13, 4, 13)),
                    elseBody =
                        listOf(
                            VariableDeclaration(
                                ComboValuePosition("let", StatementPosition(4, 1, 4, 4)),
                                ComboValuePosition("result", StatementPosition(4, 5, 4, 11)),
                                ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                                ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                                ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                                LiteralNumber(ComboValuePosition(0, StatementPosition(4, 12, 4, 13))),
                            ),
                        ),
                ),
                BooleanExpression(
                    LiteralIdentifier(ComboValuePosition("flagA", StatementPosition(5, 1, 5, 6))),
                    NotEquals(),
                    LiteralIdentifier(ComboValuePosition("flagB", StatementPosition(5, 7, 5, 12))),
                ),
                LiteralBoolean(ComboValuePosition(false, StatementPosition(6, 1, 6, 5))),
            )

        val result = linter.lint(StatementStream(statements), rules)
        println(result.results)
        assert(result.results.any { it is LintViolation })
        assertEquals(1, result.results.count { it is LintViolation }, "Expected one lint violation for snake_case identifier")
    }

    @Test
    fun `test new statements with linter rules and more cases`() {
        val statements =
            listOf(
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    ComboValuePosition("myConst", StatementPosition(1, 6, 1, 12)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Boolean", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 13, 1, 17))),
                ),
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(2, 1, 2, 5)),
                    ComboValuePosition("my_const", StatementPosition(2, 6, 2, 13)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralNumber(ComboValuePosition(42, StatementPosition(2, 14, 2, 16))),
                ),
                ConditionalExpression(
                    ComboValuePosition("if", StatementPosition(3, 1, 3, 2)),
                    BooleanExpression(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(3, 3, 3, 4))),
                        Equals(),
                        LiteralNumber(ComboValuePosition(1, StatementPosition(3, 5, 3, 6))),
                    ),
                    listOf(
                        VariableDeclaration(
                            ComboValuePosition("let", StatementPosition(3, 7, 3, 10)),
                            ComboValuePosition("result", StatementPosition(3, 11, 3, 17)),
                            ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                            ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                            ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                            LiteralNumber(ComboValuePosition(100, StatementPosition(3, 18, 3, 21))),
                        ),
                    ),
                    ComboValuePosition("(", StatementPosition(4, 1, 4, 1)),
                    ComboValuePosition(")", StatementPosition(4, 13, 4, 13)),
                    elseBody =
                        listOf(
                            VariableDeclaration(
                                ComboValuePosition("let", StatementPosition(4, 1, 4, 4)),
                                ComboValuePosition("result", StatementPosition(4, 5, 4, 11)),
                                ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                                ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                                ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                                LiteralNumber(ComboValuePosition(0, StatementPosition(4, 12, 4, 13))),
                            ),
                        ),
                ),
                BooleanExpression(
                    LiteralIdentifier(ComboValuePosition("flagA", StatementPosition(5, 1, 5, 6))),
                    NotEquals(),
                    LiteralIdentifier(ComboValuePosition("flagB", StatementPosition(5, 7, 5, 12))),
                ),
                LiteralBoolean(ComboValuePosition(false, StatementPosition(6, 1, 6, 5))),
                // Additional cases
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(7, 1, 7, 4)),
                    ComboValuePosition("anotherVar", StatementPosition(7, 5, 7, 15)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralNumber(ComboValuePosition(7, StatementPosition(7, 16, 7, 18))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(8, 1, 8, 4)),
                    ComboValuePosition("bad_var", StatementPosition(8, 5, 8, 12)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralNumber(ComboValuePosition(8, StatementPosition(8, 13, 8, 15))),
                ),
                VariableReassignment(
                    ComboValuePosition("anotherVar", StatementPosition(9, 1, 9, 10)),
                    ComboValuePosition("=", StatementPosition(10, 1, 10, 10)),
                    LiteralNumber(ComboValuePosition(99, StatementPosition(9, 11, 9, 13))),
                ),
                Grouping(
                    "(",
                    LiteralNumber(ComboValuePosition(10, StatementPosition(10, 2, 10, 4))),
                    ")",
                ),
                Unary(
                    LiteralNumber(ComboValuePosition(11, StatementPosition(11, 2, 11, 4))),
                    ComboValuePosition(MinusOperator("-"), StatementPosition(1, 1, 1, 4)),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(12, 1, 12, 7)),
                    LiteralString(ComboValuePosition("Hello!", StatementPosition(12, 8, 12, 15))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(13, 1, 13, 7)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(13, 8, 13, 9))),
                        ComboValuePosition(AdditionOperator("+"), StatementPosition(1, 1, 1, 4)),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(13, 10, 13, 11))),
                    ),
                ),
            )

        val result = linter.lint(StatementStream(statements), rules)
        println(result.results)
        assert(result.results.any { it is LintViolation })
        assert(result.results.count { it is LintViolation } == 3)
    }

    @Test
    fun `test linter with multiple analyzers and statement types`() {
        val v2Linter =
            DefaultLinterFactory.createLinter(
                Version.V2,
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

        val statements =
            listOf(
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    ComboValuePosition("goodVar", StatementPosition(1, 6, 1, 13)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Boolean", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 14, 1, 18))),
                ),
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(2, 1, 2, 5)),
                    ComboValuePosition("bad_var", StatementPosition(2, 6, 2, 13)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralNumber(ComboValuePosition(42, StatementPosition(2, 14, 2, 16))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(3, 1, 3, 4)),
                    ComboValuePosition("another_var", StatementPosition(3, 5, 3, 16)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralNumber(ComboValuePosition(7, StatementPosition(3, 17, 3, 19))),
                ),
                VariableReassignment(
                    ComboValuePosition("another_var", StatementPosition(4, 1, 4, 12)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 4)),
                    LiteralNumber(ComboValuePosition(99, StatementPosition(4, 13, 4, 15))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(5, 1, 5, 7)),
                    LiteralString(ComboValuePosition("Test", StatementPosition(5, 8, 5, 13))),
                ),
                BooleanExpression(
                    LiteralIdentifier(ComboValuePosition("flagA", StatementPosition(6, 1, 6, 6))),
                    Equals(),
                    LiteralIdentifier(ComboValuePosition("flagB", StatementPosition(6, 7, 6, 12))),
                ),
                Grouping(
                    "(",
                    LiteralNumber(ComboValuePosition(10, StatementPosition(10, 2, 10, 4))),
                    ")",
                ),
                Unary(
                    LiteralNumber(ComboValuePosition(11, StatementPosition(11, 2, 11, 4))),
                    ComboValuePosition(MinusOperator("-"), StatementPosition(1, 1, 1, 4)),
                ),
                Binary(
                    LiteralNumber(ComboValuePosition(12, StatementPosition(12, 2, 12, 3))),
                    ComboValuePosition(AdditionOperator("+"), StatementPosition(1, 1, 1, 4)),
                    LiteralNumber(ComboValuePosition(13, StatementPosition(12, 4, 12, 5))),
                ),
            )

        val v2Result = v2Linter.lint(StatementStream(statements), rules)

        assert(v2Result.results.any { it is LintViolation })
        kotlin.test.assertEquals(
            2,
            v2Result.results.count { it is LintViolation },
            "V2: Expected two lint violations for snake_case identifiers",
        )
    }

    @Test
    fun `test linter with snake_case rule and camelCase violations`() {
        val linter =
            DefaultLinterFactory.createLinter(
                Version.V2,
            )
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "snake_case", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "snake_case", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "snake_case", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
            )

        val statements =
            listOf(
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    ComboValuePosition("myConst", StatementPosition(1, 6, 1, 13)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Boolean", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 14, 1, 18))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(2, 1, 2, 4)),
                    ComboValuePosition("anotherVar", StatementPosition(2, 5, 2, 15)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 2)),
                    ComboValuePosition("Number", StatementPosition(1, 1, 1, 4)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 5)),
                    LiteralNumber(ComboValuePosition(7, StatementPosition(2, 16, 2, 18))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(3, 1, 3, 7)),
                    LiteralString(ComboValuePosition("Test", StatementPosition(3, 8, 3, 13))),
                ),
            )

        val result = linter.lint(StatementStream(statements), rules)
        assert(result.results.any { it is LintViolation })
        assertEquals(2, result.results.count { it is LintViolation }, "Expected two lint violations for camelCase identifiers")
    }

    @Test
    fun `test linter readInput restrictExpressions only literals`() {
        val statements =
            listOf(
                CallableCall(
                    ComboValuePosition("readInput", StatementPosition(1, 1, 1, 9)),
                    LiteralString(ComboValuePosition("Enter name:", StatementPosition(1, 10, 1, 22))),
                ),
                CallableCall(
                    ComboValuePosition("readInput", StatementPosition(3, 1, 3, 7)),
                    LiteralNumber(ComboValuePosition(5, StatementPosition(3, 8, 3, 13))),
                ),
                CallableCall(
                    ComboValuePosition("readInput", StatementPosition(5, 1, 5, 7)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(5, 8, 5, 9))),
                        ComboValuePosition(AdditionOperator("+"), StatementPosition(1, 1, 1, 1)),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(5, 10, 5, 11))),
                    ),
                ),
                CallableCall(
                    ComboValuePosition("readInput", StatementPosition(5, 1, 5, 7)),
                    Unary(
                        LiteralNumber(ComboValuePosition(3, StatementPosition(6, 8, 6, 9))),
                        ComboValuePosition(AdditionOperator("+"), StatementPosition(1, 1, 1, 1)),
                    ),
                ),
            )
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "snake_case", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "snake_case", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "snake_case", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
            )
        val result = linter.lint(StatementStream(statements), rules)
        assert(result.results.any { it is LintViolation })
        assertEquals(2, result.results.count { it is LintViolation }, "Expected two lint violations for restrictExpression in readInput")
    }
}
