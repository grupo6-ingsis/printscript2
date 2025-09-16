package org.gudelker.evaluator

import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.statements.interfaces.Statement

class LiteralIdentifierEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is LiteralIdentifier -> {
                val name = statement.value.value
                val value =
                    when {
                        context.hasVariable(name) -> context.getVariable(name)
                        context.hasConstant(name) -> context.getConstant(name)
                        else -> Result.failure<Any>(IllegalArgumentException("Variable o constante no declarada: $name"))
                    }
                Result.success(EvaluationResult(value, context))
            }
            else -> Result.failure(Exception("Expected LiteralIdentifier, got ${statement::class.simpleName}"))
        }
    }
}
