package org.gudelker.evaluator

import org.gudelker.Statement

interface Evaluator<T> {
    fun evaluate(
        statement: Statement,
        context: VariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult
}
