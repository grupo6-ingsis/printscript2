package org.gudelker.evaluator

import org.gudelker.BooleanExpression
import org.gudelker.Statement
import org.gudelker.comparator.Comparator
import org.gudelker.comparator.Equals
import org.gudelker.comparator.Greater
import org.gudelker.comparator.GreaterEquals
import org.gudelker.comparator.Lesser
import org.gudelker.comparator.LesserEquals
import org.gudelker.comparator.NotEquals

class BooleanExpressionEvaluator(
    private val supportedComparators: Set<Class<out Comparator>> = emptySet(),
) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is BooleanExpression -> {
                if (supportedComparators.isNotEmpty() &&
                    !supportedComparators.contains(statement.operator::class.java)
                ) {
                    throw UnsupportedOperationException("Comparador no soportado: ${statement.operator::class.simpleName}")
                }

                val leftResult = Analyzer.analyze(statement.left, context, evaluators)
                val rightResult = Analyzer.analyze(statement.right, leftResult.context, evaluators)

                val result =
                    when (statement.operator) {
                        is Equals -> performEquals(leftResult.value, rightResult.value)
                        is NotEquals -> performNotEquals(leftResult.value, rightResult.value)
                        is Greater -> performGreater(leftResult.value, rightResult.value)
                        is Lesser -> performLesser(leftResult.value, rightResult.value)
                        is GreaterEquals -> performGreaterEquals(leftResult.value, rightResult.value)
                        is LesserEquals -> performLesserEquals(leftResult.value, rightResult.value)
                    }

                EvaluationResult(result, rightResult.context)
            }
            else -> throw IllegalArgumentException("Expected BooleanExpression, got ${statement::class.simpleName}")
        }
    }

    private fun performEquals(
        left: Any,
        right: Any,
    ): Boolean {
        return left == right
    }

    private fun performNotEquals(
        left: Any,
        right: Any,
    ): Boolean {
        return left != right
    }

    private fun performGreater(
        left: Any,
        right: Any,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() > right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación >")
        }
    }

    private fun performLesser(
        left: Any,
        right: Any,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() < right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación <")
        }
    }

    private fun performGreaterEquals(
        left: Any,
        right: Any,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() >= right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación >=")
        }
    }

    private fun performLesserEquals(
        left: Any,
        right: Any,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() <= right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación <=")
        }
    }
}
