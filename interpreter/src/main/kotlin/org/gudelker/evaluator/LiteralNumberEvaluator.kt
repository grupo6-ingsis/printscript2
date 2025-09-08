package org.gudelker.evaluator

import org.gudelker.LiteralNumber
import org.gudelker.Statement

class LiteralNumberEvaluator : Evaluator<Number> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is LiteralNumber -> EvaluationResult(statement.value.value.toDouble(), context)
            else -> throw IllegalArgumentException("Expected LiteralNumber, got ${statement::class.simpleName}")
        }
    }
}
