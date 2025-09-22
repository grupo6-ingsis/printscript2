package org.gudelker.evaluator

import org.gudelker.compare.operators.Comparator
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
                if (notSupportedComparator(statement)) {
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
                                finalResult(statement, leftEvalResult, rightEvalResult)
                            },
                            onFailure = { Result.failure(it) },
                        )
                    },
                    onFailure = { Result.failure(it) },
                )
            }
            else -> Result.failure(Exception("Not evaluator for: ${statement::class.simpleName}"))
        }
    }

    private fun finalResult(
        statement: BooleanExpression,
        leftEvalResult: EvaluationResult,
        rightEvalResult: EvaluationResult,
    ): Result<EvaluationResult> =
        statement.comparator.performBinaryComparator(
            leftEvalResult.value,
            rightEvalResult.value,
        ).map { result ->
            EvaluationResult(result, rightEvalResult.context)
        }

    private fun notSupportedComparator(statement: BooleanExpression): Boolean {
        return supportedComparators.isNotEmpty() &&
            !supportedComparators.contains(statement.comparator::class.java)
    }
}
