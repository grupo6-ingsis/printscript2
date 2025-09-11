package org.gudelker.evaluator

import org.gudelker.statements.interfaces.Statement

object Analyzer {
    fun analyze(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        for (evaluator in evaluators) {
            val result =
                try {
                    evaluator.evaluate(statement, context, evaluators)
                } catch (e: IllegalArgumentException) {
                    Result.failure<EvaluationResult>(e)
                }
            if (result.isSuccess) {
                return result
            }
        }
        return Result.failure(IllegalArgumentException("No se encontró evaluador para: ${statement::class.simpleName}"))
    }
}
