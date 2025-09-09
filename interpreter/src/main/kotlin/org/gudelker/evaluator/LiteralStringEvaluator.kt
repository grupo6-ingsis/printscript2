package org.gudelker.evaluator

import org.gudelker.expressions.LiteralString
import org.gudelker.statements.interfaces.Statement

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
