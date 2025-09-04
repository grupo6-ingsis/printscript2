package org.gudelker

import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.DivisionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.operator.MultiplyOperator
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
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        println(result[0])
        assertEquals(42.0, result[0])
    }

    @Test
    fun `should interpret simple string literal`() {
        val statements =
            listOf(
                LiteralString(ComboValuePosition("Hello World", StatementPosition(1, 1, 1, 1))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals("Hello World", result[0])
    }

    @Test
    fun `should interpret binary addition`() {
        val statements =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 1, 1, 1))),
                    AdditionOperator(),
                    LiteralNumber(ComboValuePosition(3.0, StatementPosition(2, 1, 2, 1))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(8.0, result[0])
    }

    @Test
    fun `should interpret unary minus`() {
        val statements =
            listOf(
                Unary(
                    LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 1, 1, 1))),
                    MinusOperator(),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(-10.0, result[0])
    }

    @Test
    fun `should interpret variable declaration`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    "x",
                    null,
                    LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 5, 1, 5))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(Unit, result[0])
    }

    @Test
    fun `should interpret variable declaration and access`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    "x",
                    null,
                    LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 5, 1, 5))),
                ),
                LiteralIdentifier(ComboValuePosition("x", StatementPosition(2, 1, 2, 1))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0])
        assertEquals(42.0, result[1])
    }

    @Test
    fun `should interpret variable reassignment`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    "x",
                    null,
                    LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 5, 1, 5))),
                ),
                VariableReassignment(
                    ComboValuePosition("x", StatementPosition(2, 1, 2, 1)),
                    LiteralNumber(ComboValuePosition(20.0, StatementPosition(2, 5, 2, 5))),
                ),
                LiteralIdentifier(ComboValuePosition("x", StatementPosition(3, 1, 3, 1))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

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
                        MinusOperator(),
                        LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 5, 1, 5))),
                    ),
                    ")",
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(0.0, result[0])
    }

    @Test
    fun `should interpret callable println`() {
        val statements =
            listOf(
                Callable("println", LiteralString(ComboValuePosition("Hello, World!", StatementPosition(1, 1, 1, 1)))),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(Unit, result[0])
    }

    @Test
    fun `should interpret complex expression with variables`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    "x",
                    null,
                    LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 5, 1, 5))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(2, 1, 2, 1)),
                    "y",
                    null,
                    LiteralNumber(ComboValuePosition(3.0, StatementPosition(2, 5, 2, 5))),
                ),
                Binary(
                    LiteralIdentifier(ComboValuePosition("x", StatementPosition(3, 1, 3, 1))),
                    MultiplyOperator(),
                    LiteralIdentifier(ComboValuePosition("y", StatementPosition(3, 5, 3, 5))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(3, result.size)
        assertEquals(Unit, result[0])
        assertEquals(Unit, result[1])
        assertEquals(15.0, result[2])
    }

    @Test
    fun `should interpret multiple statements`() {
        val statements =
            listOf(
                LiteralNumber(ComboValuePosition(1.0, StatementPosition(1, 1, 1, 1))),
                LiteralString(ComboValuePosition("Hello", StatementPosition(2, 1, 2, 1))),
                Binary(
                    LiteralNumber(ComboValuePosition(2.0, StatementPosition(3, 1, 3, 1))),
                    AdditionOperator(),
                    LiteralNumber(ComboValuePosition(3.0, StatementPosition(3, 5, 3, 5))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(3, result.size)
        assertEquals(1.0, result[0])
        assertEquals("Hello", result[1])
        assertEquals(5.0, result[2])
    }

    @Test
    fun `should interpret binary division`() {
        val statements =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(15.0, StatementPosition(1, 1, 1, 1))),
                    DivisionOperator(),
                    LiteralNumber(ComboValuePosition(3.0, StatementPosition(1, 5, 1, 5))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(5.0, result[0])
    }

    @Test
    fun `should interpret complex binary division with variables`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    "dividend",
                    null,
                    LiteralNumber(ComboValuePosition(20.0, StatementPosition(1, 5, 1, 5))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(2, 1, 2, 1)),
                    "divisor",
                    null,
                    LiteralNumber(ComboValuePosition(4.0, StatementPosition(2, 5, 2, 5))),
                ),
                Binary(
                    LiteralIdentifier(ComboValuePosition("dividend", StatementPosition(3, 1, 3, 1))),
                    DivisionOperator(),
                    LiteralIdentifier(ComboValuePosition("divisor", StatementPosition(3, 5, 3, 5))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(3, result.size)
        assertEquals(Unit, result[0])
        assertEquals(Unit, result[1])
        assertEquals(5.0, result[2])
    }

    @Test
    fun `should interpret string concatenation with addition`() {
        val statements =
            listOf(
                Binary(
                    LiteralString(ComboValuePosition("Hello ", StatementPosition(1, 1, 1, 1))),
                    AdditionOperator(),
                    LiteralString(ComboValuePosition("World", StatementPosition(1, 8, 1, 8))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals("Hello World", result[0])
    }

    @Test
    fun `should interpret mixed string and number concatenation`() {
        val statements =
            listOf(
                Binary(
                    LiteralString(ComboValuePosition("Result: ", StatementPosition(1, 1, 1, 1))),
                    AdditionOperator(),
                    LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 10, 1, 10))),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals("Result: 42.0", result[0])
    }

    @Test
    fun `should interpret unary plus operation`() {
        val statements =
            listOf(
                Unary(
                    LiteralNumber(ComboValuePosition(15.0, StatementPosition(1, 1, 1, 1))),
                    AdditionOperator(),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(15.0, result[0])
    }

    @Test
    fun `should interpret unary plus with variable`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 1, 1, 1)),
                    "x",
                    null,
                    LiteralNumber(ComboValuePosition(-25.0, StatementPosition(1, 5, 1, 5))),
                ),
                Unary(
                    LiteralIdentifier(ComboValuePosition("x", StatementPosition(2, 1, 2, 1))),
                    AdditionOperator(),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0])
        assertEquals(-25.0, result[1])
    }

    @Test
    fun `p`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition(
                        "let",
                        StatementPosition(1, 1, 1, 1),
                    ),
                    "x",
                    null,
                    Binary(
                        LiteralNumber(ComboValuePosition(6, StatementPosition(1, 5, 1, 5))),
                        AdditionOperator(),
                        LiteralString(ComboValuePosition("hola", StatementPosition(1, 9, 1, 9))),
                    ),
                ),
            )

        val interpreter = InterpreterFactory.createInterpreter(Version.V1)
        val result = interpreter.interpret(statements)
        println(result[0])
        assertEquals(1, result.size)
        assertEquals(42.0, result[0])
    }
}
