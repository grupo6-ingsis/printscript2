package org.gudelker.evaluator

import org.gudelker.expressions.Binary
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.DivisionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.operators.MultiplyOperator
import org.gudelker.operators.Operator
import org.gudelker.statements.interfaces.Statement

class BinaryEvaluator(
    private val supportedOperators: Set<Class<out Operator>> = emptySet(),
) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is Binary -> {
                if (supportedOperators.isNotEmpty() &&
                    !supportedOperators.contains(statement.operator::class.java)
                ) {
                    throw UnsupportedOperationException("Operador no soportado: ${statement.operator::class.simpleName}")
                }

                val leftResult = Analyzer.analyze(statement.leftExpression, context, evaluators)
                val rightResult = Analyzer.analyze(statement.rightExpression, leftResult.context, evaluators)

                val result =
                    when (statement.operator) {
                        is AdditionOperator -> performAddition(leftResult.value, rightResult.value)
                        is MinusOperator -> performSubtraction(leftResult.value, rightResult.value)
                        is MultiplyOperator -> performMultiplication(leftResult.value, rightResult.value)
                        is DivisionOperator -> performDivision(leftResult.value, rightResult.value)
                    }

                EvaluationResult(result, rightResult.context)
            }
            else -> throw IllegalArgumentException("Expected Binary, got ${statement::class.simpleName}")
        }
    }

    private fun performAddition(
        left: Any,
        right: Any,
    ): Any {
        return when {
            left is Number && right is Number -> left.toDouble() + right.toDouble()
            left is String || right is String -> left.toString() + right.toString()
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
