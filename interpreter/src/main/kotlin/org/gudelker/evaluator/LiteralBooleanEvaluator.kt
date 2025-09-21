package org.gudelker.evaluator

import org.gudelker.expressions.LiteralBoolean
import org.gudelker.statements.interfaces.Statement

class LiteralBooleanEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is LiteralBoolean -> Result.success(EvaluationResult(statement.value.value, context))
            else -> return Result.failure(Exception("Not evaluator for: ${statement::class.simpleName}"))
        }
    }
}
