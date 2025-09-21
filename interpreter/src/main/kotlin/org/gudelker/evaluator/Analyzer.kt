package org.gudelker.evaluator

import org.gudelker.statements.interfaces.Statement

object Analyzer {
    fun analyze(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        var firstError: Throwable? = null
        for (evaluator in evaluators) {
            val result = evaluator.evaluate(statement, context, evaluators)
            if (result.isSuccess) return result
            val exception = result.exceptionOrNull()
            if (exception != null && exception.message != "Not evaluator for: ${statement::class.simpleName}") {
                firstError = exception
            }
        }
        return if (firstError != null) {
            Result.failure(firstError)
        } else {
            Result.failure(IllegalArgumentException("Not evaluator for: ${statement::class.simpleName}"))
        }
    }
}
