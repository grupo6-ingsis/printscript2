package org.gudelker.evaluator

import org.gudelker.LiteralString
import org.gudelker.Statement

class LiteralStringEvaluator : Evaluator<String> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is LiteralString -> EvaluationResult(statement.value.value, context)
            else -> throw IllegalArgumentException("Expected LiteralString, got ${statement::class.simpleName}")
        }
    }
}
