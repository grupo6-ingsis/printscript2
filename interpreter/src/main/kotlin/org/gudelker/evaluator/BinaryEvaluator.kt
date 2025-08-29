package org.gudelker.evaluator

import org.gudelker.Binary
import org.gudelker.Statement
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.DivisionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.operator.MultiplyOperator

class BinaryEvaluator(
    private val evaluators: List<Evaluator<out Any>>,
) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        return when (statement) {
            is Binary -> {
                val leftResult = findAndEvaluate(statement.leftExpression, context)
                val rightResult = findAndEvaluate(statement.rightExpression, leftResult.context)

                val result =
                    when (statement.operator) {
                        is AdditionOperator -> performAddition(leftResult.value, rightResult.value)
                        is MinusOperator -> performSubtraction(leftResult.value, rightResult.value)
                        is MultiplyOperator -> performMultiplication(leftResult.value, rightResult.value)
                        is DivisionOperator -> performDivision(leftResult.value, rightResult.value)
                        else -> throw UnsupportedOperationException("Operador no soportado: ${statement.operator}")
                    }

                EvaluationResult(result, rightResult.context)
            }
            else -> throw IllegalArgumentException("Expected Binary, got ${statement::class.simpleName}")
        }
    }

    private fun findAndEvaluate(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        for (evaluator in evaluators) {
            try {
                return evaluator.evaluate(statement, context)
            } catch (e: IllegalArgumentException) {
                continue
            }
        }
        throw IllegalArgumentException("No se encontró evaluador para: ${statement::class.simpleName}")
    }

    // Métodos de operaciones sin cambios...
    private fun performAddition(
        left: Any,
        right: Any,
    ): Any {
        return when {
            left is Number && right is Number -> left.toDouble() + right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para suma")
        }
    }

    private fun performSubtraction(
        left: Any,
        right: Any,
    ): Any {
        return when {
            left is Number && right is Number -> left.toDouble() - right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para resta")
        }
    }

    private fun performMultiplication(
        left: Any,
        right: Any,
    ): Any {
        return when {
            left is Number && right is Number -> left.toDouble() * right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para multiplicación")
        }
    }

    private fun performDivision(
        left: Any,
        right: Any,
    ): Any {
        return when {
            left is Number && right is Number -> {
                if (right.toDouble() == 0.0) {
                    throw ArithmeticException("División por cero")
                }
                left.toDouble() / right.toDouble()
            }
            else -> throw IllegalArgumentException("Tipos incompatibles para división")
        }
    }
}
