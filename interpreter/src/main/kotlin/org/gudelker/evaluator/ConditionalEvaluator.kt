package org.gudelker.evaluator

import org.gudelker.expressions.ConditionalExpression
import org.gudelker.statements.interfaces.Statement

class ConditionalEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is ConditionalExpression -> evaluateConditional(statement, context, evaluators)
            else -> Result.failure(unsupportedStatementError(statement))
        }
    }

    private fun evaluateConditional(
        statement: ConditionalExpression,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return evaluateCondition(statement, context, evaluators).fold(
            onSuccess = { conditionEval ->
                val conditionValue = conditionEval.value
                if (conditionValue !is Boolean) {
                    return Result.failure(invalidConditionTypeError(conditionValue))
                }
                val bodyToExecute = selectBody(statement, conditionValue)
                executeBody(bodyToExecute, conditionEval.context, evaluators)
            },
            onFailure = { Result.failure(it) },
        )
    }

    private fun evaluateCondition(
        statement: ConditionalExpression,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return Analyzer.analyze(statement.condition, context, evaluators)
    }

    private fun selectBody(
        statement: ConditionalExpression,
        conditionValue: Boolean,
    ): List<Statement> {
        return if (conditionValue) statement.ifBody else statement.elseBody ?: emptyList()
    }

    private fun executeBody(
        body: List<Statement>,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return body.fold(Result.success(EvaluationResult(Unit, context))) { accResult, stmt ->
            accResult.fold(
                onSuccess = { acc -> Analyzer.analyze(stmt, acc.context, evaluators) },
                onFailure = { Result.failure(it) },
            )
        }
    }

    private fun invalidConditionTypeError(value: Any?) =
        Exception("La condición debe evaluar a un booleano, pero fue: ${value?.let { it::class.simpleName }}")

    private fun unsupportedStatementError(statement: Statement) = Exception("Not evaluator for: ${statement::class.simpleName}")
}
