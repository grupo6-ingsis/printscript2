package org.gudelker.evaluator

import org.gudelker.expressions.Binary
import org.gudelker.operators.BinaryOperator
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
                if (operatorNotSupported(statement)) {
                    return Result.failure(UnsupportedOperationException("Operador no soportado: ${statement.operator::class.simpleName}"))
                }
                val leftResult = Analyzer.analyze(statement.leftExpression, context, evaluators)

                leftResult.fold(
                    onSuccess = { leftEvalResult ->
                        val rightResult = Analyzer.analyze(statement.rightExpression, leftEvalResult.context, evaluators)

                        rightResult.fold(
                            onSuccess = { rightEvalResult ->
                                val operator = statement.operator.value
                                evaluationResult(operator, leftEvalResult, rightEvalResult)
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

    private fun evaluationResult(
        operator: Operator,
        leftEvalResult: EvaluationResult,
        rightEvalResult: EvaluationResult,
    ): Result<EvaluationResult> =
        if (operator is BinaryOperator) {
            operator.performBinaryOperation(leftEvalResult.value, rightEvalResult.value)
                .map { EvaluationResult(it, rightEvalResult.context) }
        } else {
            Result.failure(UnsupportedOperationException("Operador binario no soportado: ${operator::class.simpleName}"))
        }

    private fun operatorNotSupported(statement: Binary): Boolean {
        return supportedOperators.isNotEmpty() &&
            !supportedOperators.contains(statement.operator.value::class.java)
    }
}
