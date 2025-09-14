package org.gudelker

import org.gudelker.compare.operators.Greater
import org.gudelker.expressions.Binary
import org.gudelker.expressions.BooleanExpression
import org.gudelker.expressions.Callable
import org.gudelker.expressions.CallableCall
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.expressions.Unary
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.DivisionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.operators.MultiplyOperator
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.VariableReassignment
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.utilities.Version
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {
    //  @Test
    fun `test espacio antes de dos puntos en declaracion - con espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition(":", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("number", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                LiteralNumber(ComboValuePosition(5, StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let x : Number = 5;\n", result)
        assertEquals("let x : Number = 5;\n", result2)
    }

    // @Test
    fun `test espacio antes de dos puntos en declaracion - sin espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition(":", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("number", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                LiteralNumber(ComboValuePosition(5, StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let x: Number = 5;\n", result)
        assertEquals("let x: Number = 5;\n", result2)
    }

    // @Test
    fun `test espacio despues de dos puntos en declaracion - con espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition(":", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("string", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                LiteralString(ComboValuePosition("hello", StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let y:String=\"hello\";\n", result)
        assertEquals("let y:String=\"hello\";\n", result2)
    }

    // @Test
    fun `test espacio despues de dos puntos en declaracion - sin espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition(":", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("string", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                LiteralString(ComboValuePosition("hello", StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let y:String = \"hello\";\n", result)
        assertEquals("let y:String = \"hello\";\n", result2)
    }

    // @Test
    fun `test espacio antes y despues del igual en asignacion - con espacios`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition(":", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("number", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                LiteralNumber(ComboValuePosition(10, StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let x:Number = 10;\n", result)
        assertEquals("let x:Number = 10;\n", result2)
    }

    // @Test
    fun `test espacio antes y despues del igual en asignacion - sin espacios`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition(":", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("number", StatementPosition(1, 1, 1, 1)),
                ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                LiteralNumber(ComboValuePosition(10, StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let x:Number=10;\n", result)
        assertEquals("let x:Number=10;\n", result2)
    }

    @Test
    fun `test salto de linea antes de println - 0 espacios`() {
        val printlnStatement =
            Callable(
                ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                LiteralString(ComboValuePosition("test", StatementPosition(1, 1, 1, 6))),
            )
        val rules =
            mapOf(
                "line-breaks-after-println" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(printlnStatement, rules)
        val result2 = formatter2.format(printlnStatement, rules)

        assertEquals("println(\"test\");\n", result)
        assertEquals("println(\"test\");\n", result2)
    }

    @Test
    fun `test salto de linea antes de println - 1 espacio`() {
        val printlnStatement =
            Callable(
                ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                LiteralString(ComboValuePosition("test", StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "line-breaks-after-println" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(printlnStatement, rules)
        val result2 = formatter2.format(printlnStatement, rules)

        assertEquals("\nprintln(\"test\");\n", result)
        assertEquals("\nprintln(\"test\");\n", result2)
    }

    @Test
    fun `test salto de linea antes de println - 2 espacios`() {
        val printlnStatement =
            Callable(
                ComboValuePosition("println", StatementPosition(1, 2, 3, 4)),
                LiteralString(ComboValuePosition("test", StatementPosition(1, 1, 1, 2))),
            )
        val rules =
            mapOf(
                "line-breaks-after-println" to FormatterRule(on = true, quantity = 2),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(printlnStatement, rules)
        val result2 = formatter2.format(printlnStatement, rules)

        assertEquals("\n\nprintln(\"test\");\n", result)
        assertEquals("\n\nprintln(\"test\");\n", result2)
    }

    @Test
    fun `test espacio antes y despues de operador binario`() {
        val binaryExpression =
            Binary(
                LiteralNumber(ComboValuePosition(5, StatementPosition(1, 1, 1, 1))),
                ComboValuePosition(AdditionOperator(), StatementPosition(1, 1, 1, 1)),
                LiteralNumber(ComboValuePosition(3, StatementPosition(1, 1, 1, 1))),
            )
        val rules = emptyMap<String, FormatterRule>()

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(binaryExpression, rules)
        val result2 = formatter2.format(binaryExpression, rules)

        assertEquals("5 + 3", result)
        assertEquals("5 + 3", result2)
    }

    @Test
    fun `test operadores con diferentes tipos`() {
        val expressions =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(10, StatementPosition(1, 2, 2, 3))),
                    ComboValuePosition(MinusOperator(), StatementPosition(1, 1, 1, 1)),
                    LiteralNumber(
                        ComboValuePosition(5, StatementPosition(1, 2, 2, 3)),
                    ),
                ),
                Binary(
                    LiteralNumber(ComboValuePosition(4, StatementPosition(1, 2, 3, 4))),
                    ComboValuePosition(MultiplyOperator(), StatementPosition(1, 1, 1, 1)),
                    LiteralNumber(
                        ComboValuePosition(6, StatementPosition(1, 2, 3, 4)),
                    ),
                ),
                Binary(
                    LiteralNumber(ComboValuePosition(8, StatementPosition(1, 2, 3, 4))),
                    ComboValuePosition(DivisionOperator(), StatementPosition(1, 1, 1, 1)),
                    LiteralNumber(
                        ComboValuePosition(2, StatementPosition(1, 2, 3, 4)),
                    ),
                ),
            )
        val rules = emptyMap<String, FormatterRule>()
        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val expectedResults = listOf("10 - 5", "4 * 6", "8 / 2")

        expressions.forEachIndexed { index, expression ->
            val result = formatter.format(expression, rules)
            assertEquals(expectedResults[index], result)
        }
        expressions.forEachIndexed { index, expression ->
            val result2 = formatter2.format(expression, rules)
            assertEquals(expectedResults[index], result2)
        }
    }

    // @Test
    fun `test formato completo con todas las reglas`() {
        val statements =
            listOf(
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                    ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                    ComboValuePosition(":", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("string", StatementPosition(1, 1, 1, 1)),
                    ComboValuePosition("=", StatementPosition(1, 1, 1, 1)),
                    LiteralString(ComboValuePosition("hello", StatementPosition(1, 1, 1, 1))),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                    ComboValuePosition("name", StatementPosition(1, 5, 1, 9)),
                    ComboValuePosition(":", StatementPosition(1, 5, 1, 9)),
                    ComboValuePosition("string", StatementPosition(1, 5, 1, 1)),
                    ComboValuePosition("=", StatementPosition(1, 5, 1, 1)),
                    LiteralString(
                        ComboValuePosition("John", StatementPosition(1, 1, 1, 1)),
                    ),
                ),
                Binary(
                    LiteralIdentifier(ComboValuePosition("x", StatementPosition(1, 2, 3, 4))),
                    ComboValuePosition(AdditionOperator(), StatementPosition(1, 1, 1, 1)),
                    LiteralNumber(
                        ComboValuePosition(10, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
            )

        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val results = statements.map { formatter.format(it, rules) }

        val expected =
            listOf(
                "let x : Number = 5;\n",
                "let name : String = \"John\";\n",
                "x + 10",
            )

        assertEquals(expected, results)

        val results2 = statements.map { formatter2.format(it, rules) }

        val expected2 =
            listOf(
                "let x : Number = 5;\n",
                "let name : String = \"John\";\n",
                "x + 10",
            )

        assertEquals(expected2, results2)
    }

    // @Test
    fun `test declaracion sin tipo`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                null,
                null,
                ComboValuePosition("=", StatementPosition(1, 5, 1, 9)),
                LiteralNumber(
                    ComboValuePosition(42, StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let x = 42;\n", result)
        assertEquals("let x = 42;\n", result2)
    }

    // @Test
    fun `test multiples espacios configurables`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("variable", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition(":", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("Boolean", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("=", StatementPosition(1, 5, 1, 9)),
                LiteralString(
                    ComboValuePosition("true", StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 3),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 2),
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 4),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("let variable   :  Boolean    =    \"true\";\n", result)
        assertEquals("let variable   :  Boolean    =    \"true\";\n", result2)
    }

    @Test
    fun `test variable reassignment con espacios`() {
        val statement =
            VariableReassignment(
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("=", StatementPosition(1, 5, 1, 9)),
                LiteralNumber(
                    ComboValuePosition(42, StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("x = 42;\n", result)
        assertEquals("x = 42;\n", result2)
    }

    @Test
    fun `test variable reassignment sin espacios`() {
        val statement =
            VariableReassignment(
                ComboValuePosition("variable", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("=", StatementPosition(1, 5, 1, 9)),
                LiteralString(ComboValuePosition("newValue", StatementPosition(1, 5, 1, 9))),
            )
        val rules =
            mapOf(
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("variable=\"newValue\";\n", result)
        assertEquals("variable=\"newValue\";\n", result2)
    }

    @Test
    fun `test variable reassignment con multiples espacios`() {
        val statement =
            VariableReassignment(
                ComboValuePosition("count", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("=", StatementPosition(1, 5, 1, 9)),
                Binary(
                    LiteralIdentifier(ComboValuePosition("count", StatementPosition(1, 2, 3, 4))),
                    ComboValuePosition(AdditionOperator(), StatementPosition(1, 2, 3, 4)),
                    LiteralNumber(
                        ComboValuePosition(1, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
            )
        val rules =
            mapOf(
                "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 3),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("count   =   count + 1;\n", result)
        assertEquals("count   =   count + 1;\n", result2)
    }

    @Test
    fun `test unary minus operator`() {
        val statement =
            Unary(
                LiteralNumber(ComboValuePosition(10, StatementPosition(1, 2, 3, 4))),
                ComboValuePosition(MinusOperator(), StatementPosition(1, 2, 3, 4)),
            )
        val rules = emptyMap<String, FormatterRule>()

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("-10", result)
        assertEquals("-10", result2)
    }

    @Test
    fun `test unary plus operator`() {
        val statement =
            Unary(
                LiteralNumber(ComboValuePosition(5, StatementPosition(1, 2, 3, 4))),
                ComboValuePosition(AdditionOperator(), StatementPosition(1, 2, 3, 4)),
            )
        val rules = emptyMap<String, FormatterRule>()

        val formatter = DefaultFormatterFactory.createFormatter(Version.V1)
        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter2.format(statement, rules)

        assertEquals("+5", result)
        assertEquals("+5", result2)
    }

    @Test
    fun `test if indentation - con 2 espacios`() {
        val statement =
            ConditionalExpression(
                ComboValuePosition("if", StatementPosition(1, 1, 1, 3)),
                BooleanExpression(
                    LiteralNumber(ComboValuePosition(5, StatementPosition(1, 1, 1, 1))),
                    Greater(),
                    LiteralNumber(ComboValuePosition(3, StatementPosition(1, 1, 1, 1))),
                ),
                listOf(
                    Binary(
                        LiteralNumber(ComboValuePosition(5, StatementPosition(1, 1, 1, 1))),
                        ComboValuePosition(AdditionOperator(), StatementPosition(1, 1, 1, 1)),
                        LiteralNumber(ComboValuePosition(2, StatementPosition(1, 1, 1, 1))),
                    ),
                ),
            )
        val rules =
            mapOf(
                "indent-inside-if" to FormatterRule(on = true, quantity = 2),
            )

        val formatter2 = DefaultFormatterFactory.createFormatter(Version.V2)

        val result2 = formatter2.format(statement, rules)

        val expected = "if (5 > 3) {\n  5 + 2\n}"

        assertEquals(expected, result2)
    }

    @Test
    fun `test readInput and readEnv formatted correctly`() {
        val statement =
            CallableCall(
                ComboValuePosition("readInput", StatementPosition(1, 1, 1, 9)),
                LiteralString(ComboValuePosition("Enter your name:", StatementPosition(1, 1, 1, 20))),
            )
        val statement2 =
            CallableCall(
                ComboValuePosition("readEnv", StatementPosition(1, 1, 1, 7)),
                LiteralString(ComboValuePosition("PATH", StatementPosition(1, 1, 1, 6))),
            )
        val rules =
            mapOf(
                "indent-inside-if" to FormatterRule(on = true, quantity = 2),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V2)

        val result = formatter.format(statement, rules)
        val result2 = formatter.format(statement2, rules)

        val expected = "readInput(\"Enter your name:\");\n"
        val expected2 = "readEnv(\"PATH\");\n"

        assertEquals(expected, result)
        assertEquals(expected2, result2)
    }
}
