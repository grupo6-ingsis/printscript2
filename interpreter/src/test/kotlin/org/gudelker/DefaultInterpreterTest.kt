package org.gudelker

import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.operator.MultiplyOperator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultInterpreterTest {
    @Test
    fun `should interpret simple number literal`() {
        val statements =
            listOf(
                LiteralNumber(42.0),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(42.0, result[0])
    }

    @Test
    fun `should interpret simple string literal`() {
        val statements =
            listOf(
                LiteralString("Hello World"),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals("Hello World", result[0])
    }

    @Test
    fun `should interpret binary addition`() {
        val statements =
            listOf(
                Binary(
                    LiteralNumber(5.0),
                    AdditionOperator(),
                    LiteralNumber(3.0),
                ),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(8.0, result[0])
    }

    @Test
    fun `should interpret unary minus`() {
        val statements =
            listOf(
                Unary(
                    LiteralNumber(10.0),
                    MinusOperator(),
                ),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(-10.0, result[0])
    }

    @Test
    fun `should interpret variable declaration`() {
        val statements =
            listOf(
                VariableDeclaration("let", "x", null, LiteralNumber(42.0)),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(Unit, result[0])
    }

    @Test
    fun `should interpret variable declaration and access`() {
        val statements =
            listOf(
                VariableDeclaration("let", "x", null, LiteralNumber(42.0)),
                LiteralIdentifier("x"),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(2, result.size)
        assertEquals(Unit, result[0])
        assertEquals(42.0, result[1])
    }

    @Test
    fun `should interpret variable reassignment`() {
        val statements =
            listOf(
                VariableDeclaration("let", "x", null, LiteralNumber(10.0)),
                VariableReassignment("x", LiteralNumber(20.0)),
                LiteralIdentifier("x"),
            )

        val interpreter = DefaultInterpreter(emptyList())
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
                        LiteralNumber(5.0),
                        AdditionOperator(),
                        LiteralNumber(3.0),
                    ),
                    ")",
                ),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(8.0, result[0])
    }

    @Test
    fun `should interpret callable println`() {
        val statements =
            listOf(
                Callable("println", LiteralString("Hello World")),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(1, result.size)
        assertEquals(Unit, result[0])
    }

    @Test
    fun `should interpret complex expression with variables`() {
        val statements =
            listOf(
                VariableDeclaration("let", "x", null, LiteralNumber(5.0)),
                VariableDeclaration("let", "y", null, LiteralNumber(3.0)),
                Binary(
                    LiteralIdentifier("x"),
                    MultiplyOperator(),
                    LiteralIdentifier("y"),
                ),
            )

        val interpreter = DefaultInterpreter(emptyList())
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
                LiteralNumber(1.0),
                LiteralString("Hello"),
                Binary(
                    LiteralNumber(2.0),
                    AdditionOperator(),
                    LiteralNumber(3.0),
                ),
            )

        val interpreter = DefaultInterpreter(emptyList())
        val result = interpreter.interpret(statements)

        assertEquals(3, result.size)
        assertEquals(1.0, result[0])
        assertEquals("Hello", result[1])
        assertEquals(5.0, result[2])
    }

    @Test
    fun `should interpret with initial list`() {
        val statements =
            listOf(
                LiteralNumber(42.0),
            )

        val interpreter = DefaultInterpreter(listOf("Initial", 100))
        val result = interpreter.interpret(statements)

        assertEquals(3, result.size)
        assertEquals("Initial", result[0])
        assertEquals(100, result[1])
        assertEquals(42.0, result[2])
    }
}
