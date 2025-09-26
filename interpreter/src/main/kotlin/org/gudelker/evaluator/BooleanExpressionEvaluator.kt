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
            is BooleanExpression -> evaluateBooleanExpression(statement, context, evaluators)
            else -> Result.failure(unsupportedStatementError(statement))
        }
    }

    private fun evaluateBooleanExpression(
        statement: BooleanExpression,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        if (notSupportedComparator(statement)) {
            return Result.failure(unsupportedComparatorError(statement))
        }
        return analyzeLeft(statement, context, evaluators)
    }

    private fun analyzeLeft(
        statement: BooleanExpression,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        val leftResult = Analyzer.analyze(statement.left, context, evaluators)
        return leftResult.fold(
            onSuccess = { leftEvalResult ->
                analyzeRight(statement, leftEvalResult, evaluators)
            },
            onFailure = { Result.failure(it) },
        )
    }

    private fun analyzeRight(
        statement: BooleanExpression,
        leftEvalResult: EvaluationResult,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        val rightResult = Analyzer.analyze(statement.right, leftEvalResult.context, evaluators)
        return rightResult.fold(
            onSuccess = { rightEvalResult ->
                finalResult(statement, leftEvalResult, rightEvalResult)
            },
            onFailure = { Result.failure(it) },
        )
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

    private fun notSupportedComparator(statement: BooleanExpression): Boolean =
        supportedComparators.isNotEmpty() &&
            !supportedComparators.contains(statement.comparator::class.java)

    private fun unsupportedComparatorError(statement: BooleanExpression) =
        UnsupportedOperationException("Comparador no soportado: ${statement.comparator::class.simpleName}")

    private fun unsupportedStatementError(statement: Statement) = Exception("Not evaluator for: ${statement::class.simpleName}")
}
