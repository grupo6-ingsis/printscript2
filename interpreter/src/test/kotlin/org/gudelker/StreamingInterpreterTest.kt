package org.gudelker

import org.gudelker.compare.operators.Greater
import org.gudelker.evaluator.Evaluator
import org.gudelker.expressions.Binary
import org.gudelker.expressions.BooleanExpression
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.inputprovider.CLIInputProvider
import org.gudelker.interpreter.ListCase
import org.gudelker.interpreter.StreamingInterpreter
import org.gudelker.interpreter.StreamingInterpreterResult
import org.gudelker.operators.AdditionOperator
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.utilities.Version
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StreamingInterpreterTest {
    private lateinit var interpreter: StreamingInterpreter
    private lateinit var evaluators: List<Evaluator<out Any>>

    @BeforeEach
    fun setUp() {
        evaluators = ListCase().listForVersion(Version.V2, CLIInputProvider())
        interpreter = StreamingInterpreter(evaluators)
    }

    @Test
    fun `debería procesar correctamente literales simples`() {
        val statement = LiteralNumber(ComboValuePosition(42.0, StatementPosition(1, 1, 1, 3)))

        val result = interpreter.processStatement(statement)

        assertTrue(result is StreamingInterpreterResult.StatementEvaluated)
        assertEquals(42.0, (result as StreamingInterpreterResult.StatementEvaluated).result)

        val finalResults = interpreter.finish()
        assertEquals(1, finalResults.size)
        assertEquals(42.0, finalResults[0])
    }

    @Test
    fun `debería mantener el contexto entre declaraciones`() {
        val declaration =
            VariableDeclaration(
                keywordCombo = ComboValuePosition("let", StatementPosition(1, 1, 1, 3)),
                identifierCombo = ComboValuePosition("x", StatementPosition(1, 5, 1, 5)),
                colon = null,
                type = null,
                equals = null,
                value = LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 9, 1, 11))),
            )

        val access = LiteralIdentifier(ComboValuePosition("x", StatementPosition(2, 1, 2, 1)))

        val result1 = interpreter.processStatement(declaration)
        val result2 = interpreter.processStatement(access)

        assertTrue(result1 is StreamingInterpreterResult.StatementEvaluated)
        assertTrue(result2 is StreamingInterpreterResult.StatementEvaluated)

        assertEquals(Unit, (result1 as StreamingInterpreterResult.StatementEvaluated).result)
        assertEquals(10.0, (result2 as StreamingInterpreterResult.StatementEvaluated).result)
    }

    @Test
    fun `debería procesar correctamente operaciones binarias`() {
        val statement =
            Binary(
                LiteralNumber(ComboValuePosition(15.0, StatementPosition(1, 1, 1, 3))),
                ComboValuePosition(AdditionOperator(), StatementPosition(1, 5, 1, 5)),
                LiteralNumber(ComboValuePosition(27.0, StatementPosition(1, 7, 1, 9))),
            )

        val result = interpreter.processStatement(statement)

        assertTrue(result is StreamingInterpreterResult.StatementEvaluated)
        assertEquals(42.0, (result as StreamingInterpreterResult.StatementEvaluated).result)
    }

    @Test
    fun `debería procesar correctamente expresiones booleanas`() {
        val statement =
            BooleanExpression(
                left = LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 1, 1, 3))),
                comparator = Greater(),
                right = LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 7, 1, 7))),
            )

        val result = interpreter.processStatement(statement)

        assertTrue(result is StreamingInterpreterResult.StatementEvaluated)
        assertEquals(true, (result as StreamingInterpreterResult.StatementEvaluated).result)
    }

    @Test
    fun `debería devolver Finished después de llamar a finish`() {
        val statement = LiteralString(ComboValuePosition("texto", StatementPosition(1, 1, 1, 6)))
        interpreter.processStatement(statement)

        interpreter.finish()

        val nextStatement = LiteralNumber(ComboValuePosition(42.0, StatementPosition(2, 1, 2, 3)))
        val result = interpreter.processStatement(nextStatement)

        assertTrue(result is StreamingInterpreterResult.Finished)
    }

    @Test
    fun `debería procesar correctamente múltiples statements complejos`() {
        val declaration =
            VariableDeclaration(
                keywordCombo = ComboValuePosition("let", StatementPosition(1, 1, 1, 3)),
                identifierCombo = ComboValuePosition("x", StatementPosition(1, 5, 1, 5)),
                colon = null,
                type = null,
                equals = null,
                value = LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 9, 1, 11))),
            )

        val binaryOp =
            Binary(
                LiteralIdentifier(ComboValuePosition("x", StatementPosition(2, 1, 2, 1))),
                ComboValuePosition(AdditionOperator(), StatementPosition(2, 3, 2, 3)),
                LiteralNumber(ComboValuePosition(5.0, StatementPosition(2, 5, 2, 5))),
            )

        interpreter.processStatement(declaration)
        val result = interpreter.processStatement(binaryOp)

        assertTrue(result is StreamingInterpreterResult.StatementEvaluated)
        assertEquals(15.0, (result as StreamingInterpreterResult.StatementEvaluated).result)

        val allResults = interpreter.getResults()
        assertEquals(2, allResults.size)
        assertEquals(Unit, allResults[0])
        assertEquals(15.0, allResults[1])
    }

    @Test
    fun `debería manejar expresiones condicionales correctamente`() {
        val conditionalExpr =
            ConditionalExpression(
                ifKeyword = ComboValuePosition("if", StatementPosition(1, 1, 1, 2)),
                condition =
                    BooleanExpression(
                        left = LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 4, 1, 6))),
                        comparator = Greater(),
                        right = LiteralNumber(ComboValuePosition(5.0, StatementPosition(1, 10, 1, 10))),
                    ),
                ifBody =
                    listOf(
                        LiteralString(ComboValuePosition("condición verdadera", StatementPosition(2, 4, 2, 22))),
                    ),
                ComboValuePosition("(", StatementPosition(1, 3, 1, 3)),
                ComboValuePosition(")", StatementPosition(1, 11, 1, 11)),
                elseBody =
                    listOf(
                        LiteralString(ComboValuePosition("condición falsa", StatementPosition(4, 4, 4, 18))),
                    ),
            )

        val result = interpreter.processStatement(conditionalExpr)

        assertTrue(result is StreamingInterpreterResult.StatementEvaluated)
        assertEquals("condición verdadera", (result as StreamingInterpreterResult.StatementEvaluated).result)
    }

    @Test
    fun `debería manejar tipos incompatibles en operaciones binarias`() {
        val statement =
            Binary(
                LiteralNumber(ComboValuePosition(10.0, StatementPosition(1, 1, 1, 3))),
                ComboValuePosition(AdditionOperator(), StatementPosition(1, 5, 1, 5)),
                LiteralString(ComboValuePosition("texto", StatementPosition(1, 7, 1, 12))),
            )

        val result = interpreter.processStatement(statement)

        assertTrue(
            result is StreamingInterpreterResult.StatementEvaluated ||
                result is StreamingInterpreterResult.Error,
        )
    }
}
