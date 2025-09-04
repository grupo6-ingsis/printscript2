package org.gudelker.evaluator

import org.gudelker.Statement

object Analyzer {
    fun analyze(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        for (evaluator in evaluators) {
            try {
                return evaluator.evaluate(statement, context, evaluators)
            } catch (e: IllegalArgumentException) {
                continue
            }
        }
        throw IllegalArgumentException("No se encontró evaluador para: ${statement::class.simpleName}")
    }
}
