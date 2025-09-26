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
            is Binary -> evaluateBinary(statement, context, evaluators)
            else -> Result.failure(unsupportedStatementError(statement))
        }
    }

    private fun evaluateBinary(
        statement: Binary,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        if (operatorNotSupported(statement)) {
            return Result.failure(unsupportedOperatorError(statement))
        }
        return analyzeLeft(statement, context, evaluators)
    }

    private fun analyzeLeft(
        statement: Binary,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        val leftResult = Analyzer.analyze(statement.leftExpression, context, evaluators)
        return leftResult.fold(
            onSuccess = { leftEvalResult ->
                analyzeRight(statement, leftEvalResult, evaluators)
            },
            onFailure = { Result.failure(it) },
        )
    }

    private fun analyzeRight(
        statement: Binary,
        leftEvalResult: EvaluationResult,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        val rightResult = Analyzer.analyze(statement.rightExpression, leftEvalResult.context, evaluators)
        return rightResult.fold(
            onSuccess = { rightEvalResult ->
                evaluationResult(statement.operator.value, leftEvalResult, rightEvalResult)
            },
            onFailure = { Result.failure(it) },
        )
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

    private fun operatorNotSupported(statement: Binary): Boolean =
        supportedOperators.isNotEmpty() &&
            !supportedOperators.contains(statement.operator.value::class.java)

    private fun unsupportedOperatorError(statement: Binary) =
        UnsupportedOperationException("Operador no soportado: ${statement.operator::class.simpleName}")

    private fun unsupportedStatementError(statement: Statement) = Exception("Not evaluator for: ${statement::class.simpleName}")
}
