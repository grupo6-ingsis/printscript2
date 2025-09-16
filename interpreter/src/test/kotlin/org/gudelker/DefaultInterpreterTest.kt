package org.gudelker

import org.gudelker.compare.operators.Equals
import org.gudelker.compare.operators.Greater
import org.gudelker.compare.operators.GreaterEquals
import org.gudelker.compare.operators.Lesser
import org.gudelker.compare.operators.LesserEquals
import org.gudelker.compare.operators.NotEquals
import org.gudelker.expressions.Binary
import org.gudelker.expressions.BooleanExpression
import org.gudelker.expressions.Callable
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.expressions.Grouping
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.expressions.Unary
import org.gudelker.interpreter.InterpreterFactory
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.statements.VariableReassignment
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.utilities.Version
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultInterpreterTest {
    @Test
    fun `should interpret simple number literal`() {
        val statements =
            listOf(
                LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 1, 1, 1))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(42.0, result[0])
    }

    @Test
    fun `should interpret simple string literal`() {
        val statements =
            listOf(
                LiteralString(ComboValuePosition("Hello World", StatementPosition(1, 1, 1, 1))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals("Hello World", result[0])
    }

    @Test
    fun `should interpret binary addition`() {
        val statements =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 1, 1, 1))),
                    ComboValuePosition(AdditionOperator(), StatementPosition(1, 3, 1, 3)),
                    LiteralNumber(ComboValuePosition(3.0, StatementPosition(2, 1, 2, 1))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(8.0, result[0])
    }

    @Test
    fun `should interpret unary minus`() {
        val statements =
            listOf(
                Unary(
                    LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 1, 1, 1))),
                    ComboValuePosition(MinusOperator(), StatementPosition(1, 2, 1, 2)),
                    StatementPosition(1, 1, 1, 1),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(-10.0, result[0])
    }

    @Test
    fun `should interpret variable declaration`() {
        val statements =
            listOf(
                VariableDeclaration(
                    keywordCombo = ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    identifierCombo = ComboValuePosition("x", StatementPosition(1, 1, 1, 1)),
                    colon = null,
                    type = null,
                    equals = null,
                    value = LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 5, 1, 5))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(Unit, result[0])
    }

    @Test
    fun `should interpret variable declaration and access`() {
        val statements =
            listOf(
                VariableDeclaration(
                    keywordCombo = ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    identifierCombo = ComboValuePosition("x", StatementPosition(1, 1, 1, 1)),
                    colon = null,
                    type = null,
                    equals = null,
                    value = LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 5, 1, 5))),
                ),
                LiteralIdentifier(ComboValuePosition("x", StatementPosition(2, 1, 2, 1))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(2, result.size)
        assertEquals(Unit, result[0])
        assertEquals(42.0, result[1])
    }

    @Test
    fun `should interpret variable reassignment`() {
        val statements =
            listOf(
                VariableDeclaration(
                    keywordCombo = ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    identifierCombo = ComboValuePosition("x", StatementPosition(1, 1, 1, 1)),
                    colon = null,
                    type = null,
                    equals = null,
                    value = LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 5, 1, 5))),
                ),
                VariableReassignment(
                    ComboValuePosition("x", StatementPosition(2, 1, 2, 1)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                    LiteralNumber(ComboValuePosition(20.0, StatementPosition(2, 5, 2, 5))),
                ),
                LiteralIdentifier(ComboValuePosition("x", StatementPosition(3, 1, 3, 1))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(3, result.size)
        assertEquals(Unit, result[0])
        assertEquals(Unit, result[1])
        assertEquals(20.0, result[2])
    }

    @Test
    fun `should interpret grouping expression`() {
        val statements =
            listOf(
                Grouping(
                    "(",
                    Binary(
                        LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 5, 1, 5))),
                        ComboValuePosition(MinusOperator(), StatementPosition(1, 6, 1, 6)),
                        LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 7, 1, 7))),
                    ),
                    ")",
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(0.0, result[0])
    }

    @Test
    fun `should interpret callable println`() {
        val statements =
            listOf(
                Callable(
                    ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                    LiteralString(ComboValuePosition("Hello, World!", StatementPosition(1, 1, 1, 1))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals("Hello, World!", result[0])
    }

    @Test
    fun `should interpret simple if statement with true condition`() {
        val statements =
            listOf(
                ConditionalExpression(
                    ifKeyword = ComboValuePosition("if", StatementPosition(1, 1, 1, 3)),
                    condition =
                        BooleanExpression(
                            left = LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 5, 1, 5))),
                            comparator = Lesser(),
                            right = LiteralNumber(ComboValuePosition(3.0, StatementPosition(1, 9, 1, 9))),
                        ),
                    ifBody =
                        listOf(
                            LiteralNumber(ComboValuePosition(42.0, StatementPosition(2, 5, 2, 7))),
                        ),
                    ComboValuePosition("(", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition(")", StatementPosition(1, 1, 1, 1)),
                    elseBody =
                        listOf(
                            LiteralNumber(ComboValuePosition(0.0, StatementPosition(4, 5, 4, 5))),
                        ),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V2)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(0.0, result[0])
    }

    @Test
    fun `should interpret if statement with false condition using else body`() {
        val statements =
            listOf(
                ConditionalExpression(
                    ifKeyword = ComboValuePosition("if", StatementPosition(1, 1, 1, 3)),
                    condition =
                        BooleanExpression(
                            left = LiteralNumber(ComboValuePosition(2.0, StatementPosition(1, 5, 1, 5))),
                            comparator = Greater(),
                            right = LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 9, 1, 9))),
                        ),
                    ifBody =
                        listOf(
                            LiteralString(ComboValuePosition("true path", StatementPosition(2, 5, 2, 15))),
                        ),
                    ComboValuePosition("(", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition(")", StatementPosition(1, 1, 1, 1)),
                    elseBody =
                        listOf(
                            LiteralString(ComboValuePosition("false path", StatementPosition(4, 5, 4, 16))),
                        ),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V2)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals("false path", result[0])
    }

    @Test
    fun `should evaluate equals with same numbers`() {
        val statement =
            BooleanExpression(
                LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 1, 1, 1))),
                LesserEquals(),
                LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 1, 1, 1))),
            )
        val statements = listOf(statement)

        val interpreter = InterpreterFactory.createInterpreter(Version.V2)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(true, result[0])
    }

    @Test
    fun `should evaluate equals with different numbers`() {
        val statement =
            BooleanExpression(
                LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 1, 1, 1))),
                Equals(),
                LiteralNumber(ComboValuePosition(3.0, StatementPosition(1, 1, 1, 1))),
            )

        val statements = listOf(statement)

        val interpreter = InterpreterFactory.createInterpreter(Version.V2)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(false, result[0])
    }

    @Test
    fun `should evaluate not equals with different values`() {
        val statement =
            BooleanExpression(
                LiteralString(ComboValuePosition("hello", StatementPosition(1, 1, 1, 1))),
                NotEquals(),
                LiteralString(ComboValuePosition("world", StatementPosition(1, 1, 1, 1))),
            )

        val statements = listOf(statement)

        val interpreter = InterpreterFactory.createInterpreter(Version.V2)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(true, result[0])
    }

    @Test
    fun `should throw exception for unsupported comparator`() {
        val statement =
            BooleanExpression(
                LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 1, 1, 1))),
                GreaterEquals(),
                LiteralNumber(ComboValuePosition(3.0, StatementPosition(1, 1, 1, 1))),
            )

        val statements = listOf(statement)

        val interpreter = InterpreterFactory.createInterpreter(Version.V2)
        val result = interpreter.interpret(statements).getOrThrow()

        assertEquals(1, result.size)
        assertEquals(true, result[0])
    }
}
