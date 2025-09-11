package org.gudelker.evaluator

import org.gudelker.compare.operators.Comparator
import org.gudelker.compare.operators.Equals
import org.gudelker.compare.operators.Greater
import org.gudelker.compare.operators.GreaterEquals
import org.gudelker.compare.operators.Lesser
import org.gudelker.compare.operators.LesserEquals
import org.gudelker.compare.operators.NotEquals
import org.gudelker.expressions.BooleanExpression
import org.gudelker.statements.interfaces.Statement

class BooleanExpressionEvaluator(
    private val supportedComparators: Set<Class<out Comparator>> = emptySet(),
) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is BooleanExpression -> {
                if (supportedComparators.isNotEmpty() &&
                    !supportedComparators.contains(statement.comparator::class.java)
                ) {
                    return Result.failure(
                        UnsupportedOperationException("Comparador no soportado: ${statement.comparator::class.simpleName}"),
                    )
                }

                val leftResult = Analyzer.analyze(statement.left, context, evaluators)
                leftResult.fold(
                    onSuccess = { leftEvalResult ->
                        val rightResult = Analyzer.analyze(statement.right, leftEvalResult.context, evaluators)
                        rightResult.fold(
                            onSuccess = { rightEvalResult ->
                                val result =
                                    when (statement.comparator) {
                                        is Equals -> performEquals(leftEvalResult.value, rightEvalResult.value)
                                        is NotEquals -> performNotEquals(leftEvalResult.value, rightEvalResult.value)
                                        is Greater -> performGreater(leftEvalResult.value, rightEvalResult.value)
                                        is Lesser -> performLesser(leftEvalResult.value, rightEvalResult.value)
                                        is GreaterEquals -> performGreaterEquals(leftEvalResult.value, rightEvalResult.value)
                                        is LesserEquals -> performLesserEquals(leftEvalResult.value, rightEvalResult.value)
                                    }
                                Result.success(EvaluationResult(result, rightEvalResult.context))
                            },
                            onFailure = { Result.failure(it) },
                        )
                    },
                    onFailure = { Result.failure(it) },
                )
            }
            else -> Result.failure(IllegalArgumentException("Expected BooleanExpression, got ${statement::class.simpleName}"))
        }
    }

    private fun performEquals(
        left: Any?,
        right: Any?,
    ): Boolean {
        return left == right
    }

    private fun performNotEquals(
        left: Any?,
        right: Any?,
    ): Boolean {
        return left != right
    }

    private fun performGreater(
        left: Any?,
        right: Any?,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() > right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación >")
        }
    }

    private fun performLesser(
        left: Any?,
        right: Any?,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() < right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación <")
        }
    }

    private fun performGreaterEquals(
        left: Any?,
        right: Any?,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() >= right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación >=")
        }
    }

    private fun performLesserEquals(
        left: Any?,
        right: Any?,
    ): Boolean {
        return when {
            left is Number && right is Number -> left.toDouble() <= right.toDouble()
            else -> throw IllegalArgumentException("Tipos incompatibles para comparación <=")
        }
    }
}
