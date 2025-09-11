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
    ): Result<EvaluationResult> {
        return when (statement) {
            is Binary -> {
                if (supportedOperators.isNotEmpty() &&
                    !supportedOperators.contains(statement.operator::class.java)
                ) {
                    return Result.failure(UnsupportedOperationException("Operador no soportado: ${statement.operator::class.simpleName}"))
                }

                val leftResult = Analyzer.analyze(statement.leftExpression, context, evaluators)

                leftResult.fold(
                    onSuccess = { leftEvalResult ->
                        val rightResult = Analyzer.analyze(statement.rightExpression, leftEvalResult.context, evaluators)

                        rightResult.fold(
                            onSuccess = { rightEvalResult ->
                                when (statement.operator) {
                                    is AdditionOperator ->
                                        performAddition(leftEvalResult.value, rightEvalResult.value)
                                            .map { EvaluationResult(it, rightEvalResult.context) }
                                    is MinusOperator ->
                                        performSubtraction(leftEvalResult.value, rightEvalResult.value)
                                            .map { EvaluationResult(it, rightEvalResult.context) }
                                    is MultiplyOperator ->
                                        performMultiplication(leftEvalResult.value, rightEvalResult.value)
                                            .map { EvaluationResult(it, rightEvalResult.context) }
                                    is DivisionOperator ->
                                        performDivision(leftEvalResult.value, rightEvalResult.value)
                                            .map { EvaluationResult(it, rightEvalResult.context) }
                                }
                            },
                            onFailure = { Result.failure(it) },
                        )
                    },
                    onFailure = { Result.failure(it) },
                )
            }
            else -> Result.failure(IllegalArgumentException("Expected Binary, got ${statement::class.simpleName}"))
        }
    }

    private fun performAddition(
        left: Any?,
        right: Any?,
    ): Result<Any> {
        return when {
            left is Number && right is Number -> Result.success(left.toDouble() + right.toDouble())
            left is String || right is String -> Result.success(left.toString() + right.toString())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para suma"))
        }
    }

    private fun performSubtraction(
        left: Any?,
        right: Any?,
    ): Result<Double> {
        return when {
            left is Number && right is Number -> Result.success(left.toDouble() - right.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para resta"))
        }
    }

    private fun performMultiplication(
        left: Any?,
        right: Any?,
    ): Result<Double> {
        return when {
            left is Number && right is Number -> Result.success(left.toDouble() * right.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para multiplicación"))
        }
    }

    private fun performDivision(
        left: Any?,
        right: Any?,
    ): Result<Double> {
        return when {
            left is Number && right is Number -> {
                if (right.toDouble() == 0.0) {
                    Result.failure(ArithmeticException("División por cero"))
                } else {
                    Result.success(left.toDouble() / right.toDouble())
                }
            }
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para división"))
        }
    }
}
