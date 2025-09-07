package org.gudelker
import org.gudelker.comparator.Equals
import org.gudelker.comparator.NotEquals
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.result.LintViolation
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
            "identifierFormat" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
            "restrictPrintlnExpressions" to LinterConfig(identifierFormat = "camelCase", restrictPrintlnExpressions = true),
        )

    @BeforeEach
    fun setUp() {
        linter = DefaultLinterFactory.createLinter(Version.V2)
    }

    @Test
    fun `test new statements with linter rules`() {
        val statements =
            listOf(
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    ComboValuePosition("myConst", StatementPosition(1, 6, 1, 12)),
                    "Boolean",
                    LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 13, 1, 17))),
                ),
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(2, 1, 2, 5)),
                    ComboValuePosition("my_const", StatementPosition(2, 6, 2, 13)),
                    "Number",
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
                            "Number",
                            LiteralNumber(ComboValuePosition(100, StatementPosition(3, 18, 3, 21))),
                        ),
                    ),
                    elseBody =
                        listOf(
                            VariableDeclaration(
                                ComboValuePosition("let", StatementPosition(4, 1, 4, 4)),
                                ComboValuePosition("result", StatementPosition(4, 5, 4, 11)),
                                "Number",
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
                    "Boolean",
                    LiteralBoolean(ComboValuePosition(true, StatementPosition(1, 13, 1, 17))),
                ),
                ConstDeclaration(
                    ComboValuePosition("const", StatementPosition(2, 1, 2, 5)),
                    ComboValuePosition("my_const", StatementPosition(2, 6, 2, 13)),
                    "Number",
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
                            "Number",
                            LiteralNumber(ComboValuePosition(100, StatementPosition(3, 18, 3, 21))),
                        ),
                    ),
                    elseBody =
                        listOf(
                            VariableDeclaration(
                                ComboValuePosition("let", StatementPosition(4, 1, 4, 4)),
                                ComboValuePosition("result", StatementPosition(4, 5, 4, 11)),
                                "Number",
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
                    "Number",
                    LiteralNumber(ComboValuePosition(7, StatementPosition(7, 16, 7, 18))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(8, 1, 8, 4)),
                    ComboValuePosition("bad_var", StatementPosition(8, 5, 8, 12)),
                    "Number",
                    LiteralNumber(ComboValuePosition(8, StatementPosition(8, 13, 8, 15))),
                ),
                VariableReassignment(
                    ComboValuePosition("anotherVar", StatementPosition(9, 1, 9, 10)),
                    LiteralNumber(ComboValuePosition(99, StatementPosition(9, 11, 9, 13))),
                ),
                Grouping(
                    "(",
                    LiteralNumber(ComboValuePosition(10, StatementPosition(10, 2, 10, 4))),
                    ")",
                ),
                Unary(
                    LiteralNumber(ComboValuePosition(11, StatementPosition(11, 2, 11, 4))),
                    MinusOperator(),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(12, 1, 12, 7)),
                    LiteralString(ComboValuePosition("Hello!", StatementPosition(12, 8, 12, 15))),
                ),
                Callable(
                    ComboValuePosition("println", StatementPosition(13, 1, 13, 7)),
                    Binary(
                        LiteralNumber(ComboValuePosition(1, StatementPosition(13, 8, 13, 9))),
                        AdditionOperator(),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(13, 10, 13, 11))),
                    ),
                ),
            )

        val result = linter.lint(StatementStream(statements), rules)
        println(result.results)
        assert(result.results.any { it is LintViolation })
        assert(result.results.count { it is LintViolation } == 3)
    }
}
