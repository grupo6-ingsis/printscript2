package org.gudelker

import org.gudelker.analyzer.BinaryAnalyzer
import org.gudelker.analyzer.CallableAnalyzer
import org.gudelker.analyzer.GroupingAnalyzer
import org.gudelker.analyzer.LiteralIdentifierAnalyzer
import org.gudelker.analyzer.LiteralNumberAnalyzer
import org.gudelker.analyzer.LiteralStringAnalyzer
import org.gudelker.analyzer.UnaryAnalyzer
import org.gudelker.analyzer.VariableDeclarationAnalyzer
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.DivisionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.operator.MultiplyOperator
import org.gudelker.rules.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {
    private fun createFormatter(): DefaultFormatter {
        return DefaultFormatter(
            listOf(
                VariableDeclarationAnalyzer(),
                LiteralNumberAnalyzer(),
                LiteralIdentifierAnalyzer(),
                LiteralStringAnalyzer(),
                GroupingAnalyzer(),
                UnaryAnalyzer(),
                CallableAnalyzer(),
                BinaryAnalyzer(),
            ),
        )
    }

    @Test
    fun `test espacio antes de dos puntos en declaracion - con espacio`() {
        val statement = VariableDeclaration("let", "x", "Int", LiteralNumber(5))
        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 1),
                "afterDeclaration" to Rule(on = true, quantity = 1),
                "assignDeclaration" to Rule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x : Int = 5;", result)
    }

    @Test
    fun `test espacio antes de dos puntos en declaracion - sin espacio`() {
        val statement = VariableDeclaration("let", "x", "Int", LiteralNumber(5))
        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 0),
                "afterDeclaration" to Rule(on = true, quantity = 1),
                "assignDeclaration" to Rule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x: Int = 5;", result)
    }

    @Test
    fun `test espacio despues de dos puntos en declaracion - con espacio`() {
        val statement = VariableDeclaration("let", "y", "String", LiteralString("hello"))
        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 0),
                "afterDeclaration" to Rule(on = true, quantity = 0),
                "assignDeclaration" to Rule(on = true, quantity = 0),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let y:String=\"hello\";", result)
    }

    @Test
    fun `test espacio despues de dos puntos en declaracion - sin espacio`() {
        val statement = VariableDeclaration("let", "y", "String", LiteralString("hello"))
        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 0),
                "afterDeclaration" to Rule(on = true, quantity = 0),
                "assignDeclaration" to Rule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let y:String = \"hello\";", result)
    }

    @Test
    fun `test espacio antes y despues del igual en asignacion - con espacios`() {
        val statement = VariableDeclaration("let", "x", "Int", LiteralNumber(10))
        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 0),
                "afterDeclaration" to Rule(on = true, quantity = 0),
                "assignDeclaration" to Rule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x:Int = 10;", result)
    }

    @Test
    fun `test espacio antes y despues del igual en asignacion - sin espacios`() {
        val statement = VariableDeclaration("let", "x", "Int", LiteralNumber(10))
        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 0),
                "afterDeclaration" to Rule(on = true, quantity = 0),
                "assignDeclaration" to Rule(on = true, quantity = 0),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x:Int=10;", result)
    }

    @Test
    fun `test salto de linea antes de println - 0 espacios`() {
        val printlnStatement = Callable("println", LiteralString("test"))
        val rules =
            mapOf(
                "println" to Rule(on = true, quantity = 0),
            )

        val formatter = createFormatter()
        val result = formatter.format(printlnStatement, rules)

        assertEquals("println(\"test\");", result)
    }

    @Test
    fun `test salto de linea antes de println - 1 espacio`() {
        val printlnStatement = Callable("println", LiteralString("test"))
        val rules =
            mapOf(
                "println" to Rule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(printlnStatement, rules)

        assertEquals("\nprintln(\"test\");", result)
    }

    @Test
    fun `test salto de linea antes de println - 2 espacios`() {
        val printlnStatement = Callable("println", LiteralString("test"))
        val rules =
            mapOf(
                "println" to Rule(on = true, quantity = 2),
            )

        val formatter = createFormatter()
        val result = formatter.format(printlnStatement, rules)

        assertEquals("\n\nprintln(\"test\");", result)
    }

    @Test
    fun `test espacio antes y despues de operador binario`() {
        val binaryExpression =
            Binary(
                LiteralNumber(5),
                AdditionOperator(),
                LiteralNumber(3),
            )
        val rules = emptyMap<String, Rule>()

        val formatter = createFormatter()
        val result = formatter.format(binaryExpression, rules)

        assertEquals("5 + 3", result)
    }

    @Test
    fun `test operadores con diferentes tipos`() {
        val expressions =
            listOf(
                Binary(LiteralNumber(10), MinusOperator(), LiteralNumber(5)),
                Binary(LiteralNumber(4), MultiplyOperator(), LiteralNumber(6)),
                Binary(LiteralNumber(8), DivisionOperator(), LiteralNumber(2)),
            )
        val rules = emptyMap<String, Rule>()
        val formatter = createFormatter()

        val expectedResults = listOf("10 - 5", "4 * 6", "8 / 2")

        expressions.forEachIndexed { index, expression ->
            val result = formatter.format(expression, rules)
            assertEquals(expectedResults[index], result)
        }
    }

    @Test
    fun `test formato completo con todas las reglas`() {
        val statements =
            listOf(
                VariableDeclaration("let", "x", "Int", LiteralNumber(5)),
                VariableDeclaration("let", "name", "String", LiteralString("John")),
                Binary(LiteralIdentifier("x"), AdditionOperator(), LiteralNumber(10)),
            )

        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 1),
                "afterDeclaration" to Rule(on = true, quantity = 1),
                "assignDeclaration" to Rule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val results = statements.map { formatter.format(it, rules) }

        val expected =
            listOf(
                "let x : Int = 5;",
                "let name : String = \"John\";",
                "x + 10",
            )

        assertEquals(expected, results)
    }

    @Test
    fun `test declaracion sin tipo`() {
        val statement = VariableDeclaration("let", "x", null, LiteralNumber(42))
        val rules =
            mapOf(
                "assignDeclaration" to Rule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x = 42;", result)
    }

    @Test
    fun `test multiples espacios configurables`() {
        val statement = VariableDeclaration("let", "variable", "Boolean", LiteralString("true"))
        val rules =
            mapOf(
                "beforeDeclaration" to Rule(on = true, quantity = 3),
                "afterDeclaration" to Rule(on = true, quantity = 2),
                "assignDeclaration" to Rule(on = true, quantity = 4),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let variable   :  Boolean    =    \"true\";", result)
    }
}
