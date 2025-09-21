package org.gudelker

import org.gudelker.expressions.Binary
import org.gudelker.expressions.Callable
import org.gudelker.expressions.CallableCall
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.inputprovider.CLIInputProvider
import org.gudelker.interpreter.ChunkBaseFactory
import org.gudelker.operators.DivisionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.operators.MultiplyOperator
import org.gudelker.result.InvalidInterpreterResult
import org.gudelker.statements.VariableReassignment
import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.utilities.Version
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ErrorTests {
    @Test
    fun `error al usar operador no soportado`() {
        val statements =
            listOf(
                Binary(
                    LiteralString(ComboValuePosition("Hola", StatementPosition(1, 1, 1, 4))),
                    ComboValuePosition(MinusOperator(), StatementPosition(1, 6, 1, 6)),
                    LiteralString(ComboValuePosition("Mundo", StatementPosition(1, 8, 1, 12))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al usar callable inexistente`() {
        val statements =
            listOf(
                Callable(
                    ComboValuePosition("funcionInexistente", StatementPosition(1, 1, 1, 17)),
                    LiteralString(ComboValuePosition("Hola", StatementPosition(1, 19, 1, 24))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error en variable con tipo incorrecto`() {
        val statements =
            listOf(
                VariableDeclaration(
                    keywordCombo = ComboValuePosition("let", StatementPosition(1, 1, 1, 3)),
                    identifierCombo = ComboValuePosition("x", StatementPosition(1, 5, 1, 5)),
                    colon = ComboValuePosition(":", StatementPosition(1, 6, 1, 6)),
                    type = ComboValuePosition("number", StatementPosition(1, 8, 1, 13)),
                    equals = ComboValuePosition("=", StatementPosition(1, 15, 1, 15)),
                    value = LiteralString(ComboValuePosition("esto es string", StatementPosition(1, 17, 1, 30))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al reasignar variable no declarada`() {
        val statements =
            listOf(
                VariableReassignment(
                    ComboValuePosition("x", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("=", StatementPosition(1, 3, 1, 3)),
                    LiteralNumber(ComboValuePosition(20.0, StatementPosition(1, 5, 1, 5))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al intentar redeclarar una constante`() {
        val statements =
            listOf(
                ConstDeclaration(
                    keywordCombo = ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    identifierCombo = ComboValuePosition("PI", StatementPosition(1, 7, 1, 8)),
                    colon = null,
                    type = null,
                    equals = ComboValuePosition("=", StatementPosition(1, 10, 1, 10)),
                    value = LiteralNumber(ComboValuePosition(3.14, StatementPosition(1, 12, 1, 15))),
                ),
                ConstDeclaration(
                    keywordCombo = ComboValuePosition("const", StatementPosition(2, 1, 2, 5)),
                    identifierCombo = ComboValuePosition("PI", StatementPosition(2, 7, 2, 8)),
                    colon = null,
                    type = null,
                    equals = ComboValuePosition("=", StatementPosition(2, 10, 2, 10)),
                    value = LiteralNumber(ComboValuePosition(3.14159, StatementPosition(2, 12, 2, 18))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al intentar reasignar una constante`() {
        val statements =
            listOf(
                ConstDeclaration(
                    keywordCombo = ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    identifierCombo = ComboValuePosition("MAX_VALUE", StatementPosition(1, 7, 1, 15)),
                    colon = null,
                    type = null,
                    equals = ComboValuePosition("=", StatementPosition(1, 17, 1, 17)),
                    value = LiteralNumber(ComboValuePosition(100.0, StatementPosition(1, 19, 1, 23))),
                ),
                VariableReassignment(
                    ComboValuePosition("MAX_VALUE", StatementPosition(2, 1, 2, 9)),
                    ComboValuePosition("=", StatementPosition(2, 11, 2, 11)),
                    LiteralNumber(ComboValuePosition(200.0, StatementPosition(2, 13, 2, 17))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al declarar constante con tipo incorrecto`() {
        val statements =
            listOf(
                ConstDeclaration(
                    keywordCombo = ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    identifierCombo = ComboValuePosition("ID", StatementPosition(1, 7, 1, 8)),
                    colon = ComboValuePosition(":", StatementPosition(1, 9, 1, 9)),
                    type = ComboValuePosition("number", StatementPosition(1, 11, 1, 16)),
                    equals = ComboValuePosition("=", StatementPosition(1, 18, 1, 18)),
                    value = LiteralString(ComboValuePosition("ABC123", StatementPosition(1, 20, 1, 27))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al declarar constante con tipo no soportado`() {
        val statements =
            listOf(
                ConstDeclaration(
                    keywordCombo = ComboValuePosition("const", StatementPosition(1, 1, 1, 5)),
                    identifierCombo = ComboValuePosition("X", StatementPosition(1, 7, 1, 7)),
                    colon = ComboValuePosition(":", StatementPosition(1, 8, 1, 8)),
                    type = ComboValuePosition("array", StatementPosition(1, 10, 1, 14)),
                    equals = ComboValuePosition("=", StatementPosition(1, 16, 1, 16)),
                    value = LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 18, 1, 21))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assert(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al acceder a variable no declarada`() {
        val statements =
            listOf(
                VariableReassignment(
                    ComboValuePosition("y", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("=", StatementPosition(1, 3, 1, 3)),
                    LiteralNumber(ComboValuePosition(15.0, StatementPosition(1, 5, 1, 7))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)
        assertTrue(result is InvalidInterpreterResult)
    }

    @Test
    fun `error de división por cero`() {
        val statements =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 1, 1, 3))),
                    ComboValuePosition(DivisionOperator(), StatementPosition(1, 5, 1, 5)),
                    LiteralNumber(ComboValuePosition(0.0, StatementPosition(1, 7, 1, 7))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)
        assertTrue(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al declarar variable duplicada`() {
        val statements =
            listOf(
                VariableDeclaration(
                    keywordCombo = ComboValuePosition("let", StatementPosition(1, 1, 1, 3)),
                    identifierCombo = ComboValuePosition("x", StatementPosition(1, 5, 1, 5)),
                    colon = null,
                    type = null,
                    equals = null,
                    value = LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 9, 1, 12))),
                ),
                VariableDeclaration(
                    keywordCombo = ComboValuePosition("let", StatementPosition(2, 1, 2, 3)),
                    identifierCombo = ComboValuePosition("x", StatementPosition(2, 5, 2, 5)),
                    colon = null,
                    type = null,
                    equals = null,
                    value = LiteralNumber(ComboValuePosition(20.0, StatementPosition(2, 9, 2, 12))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assertTrue(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al pasar parámetros incorrectos a una función`() {
        val statements =
            listOf(
                CallableCall(
                    ComboValuePosition("readEnv", StatementPosition(1, 1, 1, 7)),
                    LiteralNumber(ComboValuePosition(123.0, StatementPosition(1, 9, 1, 11))),
                    // readEnv espera un string
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assertTrue(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al usar una función inexistente`() {
        val statements =
            listOf(
                Callable(
                    ComboValuePosition("funcionQueNoExiste", StatementPosition(1, 1, 1, 18)),
                    LiteralString(ComboValuePosition("parámetro", StatementPosition(1, 20, 1, 29))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assertTrue(result is InvalidInterpreterResult)
    }

    @Test
    fun `error al operar con tipos incompatibles`() {
        val statements =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 1, 1, 3))),
                    ComboValuePosition(MultiplyOperator(), StatementPosition(1, 5, 1, 5)),
                    LiteralString(ComboValuePosition("texto", StatementPosition(1, 7, 1, 11))),
                ),
            )

        val interpreter = ChunkBaseFactory.createInterpreter(Version.V2, CLIInputProvider())
        val result = interpreter.interpret(statements)

        assertTrue(result is InvalidInterpreterResult)
    }
}
