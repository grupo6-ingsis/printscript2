package org.gudelker.evaluator

import org.gudelker.expressions.LiteralString
import org.gudelker.statements.interfaces.Statement

class LiteralStringEvaluator : Evaluator<String> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is LiteralString -> Result.success(EvaluationResult(statement.value.value, context))
            else -> Result.failure(Exception("Expected LiteralString, got ${statement::class.simpleName}"))
        }
    }
}
