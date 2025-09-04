package org.gudelker

import org.gudelker.analyzer.BinaryForAnalyzer
import org.gudelker.analyzer.CallableForAnalyzer
import org.gudelker.analyzer.GroupingForAnalyzer
import org.gudelker.analyzer.LiteralIdentifierForAnalyzer
import org.gudelker.analyzer.LiteralNumberForAnalyzer
import org.gudelker.analyzer.LiteralStringForAnalyzer
import org.gudelker.analyzer.UnaryForAnalyzer
import org.gudelker.analyzer.VariableDeclarationForAnalyzer
import org.gudelker.analyzer.VariableReassignmentForAnalyzer
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.DivisionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.operator.MultiplyOperator
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.SpaceAfterColon
import org.gudelker.rulevalidator.SpaceBeforeColon
import org.gudelker.rulevalidator.SpacesAroundAssignation
import org.gudelker.rulevalidator.SpacesPrintln
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {
    private fun createFormatter(): DefaultFormatter {
        val ruleValidors =
            listOf(
                SpaceBeforeColon(),
                SpaceAfterColon(),
                SpacesAroundAssignation(),
            )
        return DefaultFormatter(
            listOf(
                VariableDeclarationForAnalyzer(
                    ruleValidors,
                ),
                LiteralNumberForAnalyzer(),
                LiteralIdentifierForAnalyzer(),
                LiteralStringForAnalyzer(),
                GroupingForAnalyzer(),
                UnaryForAnalyzer(),
                CallableForAnalyzer(
                    listOf(SpacesPrintln()),
                ),
                BinaryForAnalyzer(),
                VariableReassignmentForAnalyzer(
                    listOf(SpacesAroundAssignation()),
                ),
            ),
        )
    }

    @Test
    fun `test espacio antes de dos puntos en declaracion - con espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                "Number",
                LiteralNumber(ComboValuePosition(5, StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 1),
                "afterDeclaration" to FormatterRule(on = true, quantity = 1),
                "assignDeclaration" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x : Number = 5;", result)
    }

    @Test
    fun `test espacio antes de dos puntos en declaracion - sin espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                "Number",
                LiteralNumber(
                    ComboValuePosition(5, StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 0),
                "afterDeclaration" to FormatterRule(on = true, quantity = 1),
                "assignDeclaration" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x: Number = 5;", result)
    }

    @Test
    fun `test espacio despues de dos puntos en declaracion - con espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("y", StatementPosition(1, 5, 1, 9)),
                "String",
                LiteralString(
                    ComboValuePosition("hello", StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 0),
                "afterDeclaration" to FormatterRule(on = true, quantity = 0),
                "assignDeclaration" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let y:String=\"hello\";", result)
    }

    @Test
    fun `test espacio despues de dos puntos en declaracion - sin espacio`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("y", StatementPosition(1, 5, 1, 9)),
                "String",
                LiteralString(
                    ComboValuePosition("hello", StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 0),
                "afterDeclaration" to FormatterRule(on = true, quantity = 0),
                "assignDeclaration" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let y:String = \"hello\";", result)
    }

    @Test
    fun `test espacio antes y despues del igual en asignacion - con espacios`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                "Number",
                LiteralNumber(ComboValuePosition(10, StatementPosition(1, 1, 1, 1))),
            )
        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 0),
                "afterDeclaration" to FormatterRule(on = true, quantity = 0),
                "assignDeclaration" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x:Number = 10;", result)
    }

    @Test
    fun `test espacio antes y despues del igual en asignacion - sin espacios`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                "Number",
                LiteralNumber(
                    ComboValuePosition(10, StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 0),
                "afterDeclaration" to FormatterRule(on = true, quantity = 0),
                "assignDeclaration" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x:Number=10;", result)
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
                "println" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = createFormatter()
        val result = formatter.format(printlnStatement, rules)

        assertEquals("println(\"test\");", result)
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
                "println" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(printlnStatement, rules)

        assertEquals("\nprintln(\"test\");", result)
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
                "println" to FormatterRule(on = true, quantity = 2),
            )

        val formatter = createFormatter()
        val result = formatter.format(printlnStatement, rules)

        assertEquals("\n\nprintln(\"test\");", result)
    }

    @Test
    fun `test espacio antes y despues de operador binario`() {
        val binaryExpression =
            Binary(
                LiteralNumber(ComboValuePosition(5, StatementPosition(1, 1, 1, 1))),
                AdditionOperator(),
                LiteralNumber(ComboValuePosition(3, StatementPosition(1, 1, 1, 1))),
            )
        val rules = emptyMap<String, FormatterRule>()

        val formatter = createFormatter()
        val result = formatter.format(binaryExpression, rules)

        assertEquals("5 + 3", result)
    }

    @Test
    fun `test operadores con diferentes tipos`() {
        val expressions =
            listOf(
                Binary(
                    LiteralNumber(ComboValuePosition(10, StatementPosition(1, 2, 2, 3))),
                    MinusOperator(),
                    LiteralNumber(
                        ComboValuePosition(5, StatementPosition(1, 2, 2, 3)),
                    ),
                ),
                Binary(
                    LiteralNumber(ComboValuePosition(4, StatementPosition(1, 2, 3, 4))),
                    MultiplyOperator(),
                    LiteralNumber(
                        ComboValuePosition(6, StatementPosition(1, 2, 3, 4)),
                    ),
                ),
                Binary(
                    LiteralNumber(ComboValuePosition(8, StatementPosition(1, 2, 3, 4))),
                    DivisionOperator(),
                    LiteralNumber(
                        ComboValuePosition(2, StatementPosition(1, 2, 3, 4)),
                    ),
                ),
            )
        val rules = emptyMap<String, FormatterRule>()
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
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                    ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                    "Number",
                    LiteralNumber(
                        ComboValuePosition(5, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
                VariableDeclaration(
                    ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                    ComboValuePosition("name", StatementPosition(1, 5, 1, 9)),
                    "String",
                    LiteralString(
                        ComboValuePosition("John", StatementPosition(1, 1, 1, 1)),
                    ),
                ),
                Binary(
                    LiteralIdentifier(ComboValuePosition("x", StatementPosition(1, 2, 3, 4))),
                    AdditionOperator(),
                    LiteralNumber(
                        ComboValuePosition(10, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
            )

        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 1),
                "afterDeclaration" to FormatterRule(on = true, quantity = 1),
                "assignDeclaration" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val results = statements.map { formatter.format(it, rules) }

        val expected =
            listOf(
                "let x : Number = 5;",
                "let name : String = \"John\";",
                "x + 10",
            )

        assertEquals(expected, results)
    }

    @Test
    fun `test declaracion sin tipo`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                null,
                LiteralNumber(
                    ComboValuePosition(42, StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "assignDeclaration" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let x = 42;", result)
    }

    @Test
    fun `test multiples espacios configurables`() {
        val statement =
            VariableDeclaration(
                ComboValuePosition("let", StatementPosition(1, 5, 1, 9)),
                ComboValuePosition("variable", StatementPosition(1, 5, 1, 9)),
                "Boolean",
                LiteralString(
                    ComboValuePosition("true", StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "beforeDeclaration" to FormatterRule(on = true, quantity = 3),
                "afterDeclaration" to FormatterRule(on = true, quantity = 2),
                "assignDeclaration" to FormatterRule(on = true, quantity = 4),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("let variable   :  Boolean    =    \"true\";", result)
    }

    @Test
    fun `test variable reassignment con espacios`() {
        val statement =
            VariableReassignment(
                ComboValuePosition("x", StatementPosition(1, 5, 1, 9)),
                LiteralNumber(
                    ComboValuePosition(42, StatementPosition(1, 1, 1, 1)),
                ),
            )
        val rules =
            mapOf(
                "assignDeclaration" to FormatterRule(on = true, quantity = 1),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("x = 42;", result)
    }

    @Test
    fun `test variable reassignment sin espacios`() {
        val statement =
            VariableReassignment(
                ComboValuePosition("variable", StatementPosition(1, 5, 1, 9)),
                LiteralString(ComboValuePosition("newValue", StatementPosition(1, 5, 1, 9))),
            )
        val rules =
            mapOf(
                "assignDeclaration" to FormatterRule(on = true, quantity = 0),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("variable=\"newValue\";", result)
    }

    @Test
    fun `test variable reassignment con multiples espacios`() {
        val statement =
            VariableReassignment(
                ComboValuePosition("count", StatementPosition(1, 5, 1, 9)),
                Binary(
                    LiteralIdentifier(ComboValuePosition("count", StatementPosition(1, 2, 3, 4))),
                    AdditionOperator(),
                    LiteralNumber(
                        ComboValuePosition(1, StatementPosition(1, 1, 1, 1)),
                    ),
                ),
            )
        val rules =
            mapOf(
                "assignDeclaration" to FormatterRule(on = true, quantity = 3),
            )

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("count   =   count + 1;", result)
    }

    @Test
    fun `test unary minus operator`() {
        val statement = Unary(LiteralNumber(ComboValuePosition(10, StatementPosition(1, 2, 3, 4))), MinusOperator())
        val rules = emptyMap<String, FormatterRule>()

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("-10", result)
    }

    @Test
    fun `test unary plus operator`() {
        val statement = Unary(LiteralNumber(ComboValuePosition(5, StatementPosition(1, 2, 3, 4))), AdditionOperator())
        val rules = emptyMap<String, FormatterRule>()

        val formatter = createFormatter()
        val result = formatter.format(statement, rules)

        assertEquals("+5", result)
    }
}
