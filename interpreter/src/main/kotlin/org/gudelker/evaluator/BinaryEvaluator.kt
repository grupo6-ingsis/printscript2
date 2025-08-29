package org.gudelker.evaluator

import org.gudelker.Binary
import org.gudelker.Statement
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.DivisionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.operator.MultiplyOperator

class BinaryEvaluator(
    private val evaluators: List<Evaluator<Any>>,
) : Evaluator<Any> {
    override fun evaluate(statement: Statement): Any {
        return when (statement) {
            is Binary -> {
                val leftValue = findAndEvaluate(statement.leftExpression)
                val rightValue = findAndEvaluate(statement.rightExpression)

                when (statement.operator) {
                    is AdditionOperator -> performAddition(leftValue, rightValue)
                    is MinusOperator -> performSubtraction(leftValue, rightValue)
                    is MultiplyOperator -> performMultiplication(leftValue, rightValue)
                    is DivisionOperator -> performDivision(leftValue, rightValue)
                    else -> throw UnsupportedOperationException("Operador no soportado: ${statement.operator}")
                }
            }
            else -> throw IllegalArgumentException("Expected Binary, got ${statement::class.simpleName}")
        }
    }

    private fun findAndEvaluate(statement: Statement): Any {
        for (evaluator in evaluators) {
            try {
                return evaluator.evaluate(statement)
            } catch (e: IllegalArgumentException) {
                continue
            }
        }
        throw IllegalArgumentException("No se encontró evaluador para: ${statement::class.simpleName}")
    }

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
