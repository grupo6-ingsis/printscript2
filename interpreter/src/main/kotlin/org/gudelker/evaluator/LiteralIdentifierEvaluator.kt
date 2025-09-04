package org.gudelker.evaluator

import org.gudelker.LiteralIdentifier
import org.gudelker.Statement

class LiteralIdentifierEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is LiteralIdentifier -> {
                val name = statement.value.value
                val value =
                    when {
                        context.hasVariable(name) -> context.getVariable(name)
                        context.hasConstant(name) -> context.getConstant(name)
                        else -> throw IllegalArgumentException("Variable o constante no declarada: $name")
                    }
                EvaluationResult(value, context)
            }
            else -> throw IllegalArgumentException("Expected LiteralIdentifier, got ${statement::class.simpleName}")
        }
    }
}
