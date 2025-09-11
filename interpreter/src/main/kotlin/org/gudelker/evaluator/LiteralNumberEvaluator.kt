package org.gudelker.evaluator

import org.gudelker.expressions.LiteralNumber
import org.gudelker.statements.interfaces.Statement

class LiteralNumberEvaluator : Evaluator<Number> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is LiteralNumber -> Result.success(EvaluationResult(statement.value.value.toDouble(), context))
            else -> Result.failure(IllegalArgumentException("Expected LiteralNumber, got ${statement::class.simpleName}"))
        }
    }
}
