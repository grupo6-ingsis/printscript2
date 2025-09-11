package org.gudelker.evaluator

import org.gudelker.statements.interfaces.Statement

interface Evaluator<T> {
    fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult>
}
