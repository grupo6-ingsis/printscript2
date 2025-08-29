package org.gudelker.evaluator

import org.gudelker.LiteralIdentifier
import org.gudelker.Statement

class LiteralIdentifierEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        return when (statement) {
            is LiteralIdentifier -> {
                val value = context.getVariable(statement.value)
                EvaluationResult(value, context)
            }
            else -> throw IllegalArgumentException("Expected LiteralIdentifier, got ${statement::class.simpleName}")
        }
    }
}
