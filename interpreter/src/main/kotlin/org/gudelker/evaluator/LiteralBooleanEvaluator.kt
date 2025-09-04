package org.gudelker.evaluator

import org.gudelker.LiteralBoolean
import org.gudelker.Statement

class LiteralBooleanEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is LiteralBoolean -> EvaluationResult(statement.value.value, context)
            else -> throw IllegalArgumentException("Expected LiteralIdentifier, got ${statement::class.simpleName}")
        }
    }
}
