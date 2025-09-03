package org.gudelker.evaluator

import org.gudelker.LiteralIdentifier
import org.gudelker.Statement

class LiteralIdentifierEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is LiteralIdentifier -> {
                val value = context.getVariable(statement.value.value)
                EvaluationResult(value, context)
            }
            else -> throw IllegalArgumentException("Expected LiteralIdentifier, got ${statement::class.simpleName}")
        }
    }
}
