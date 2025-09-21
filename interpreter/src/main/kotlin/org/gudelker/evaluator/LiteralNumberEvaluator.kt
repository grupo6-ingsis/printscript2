package org.gudelker.evaluator

import org.gudelker.expressions.LiteralNumber
import org.gudelker.statements.interfaces.Statement
import kotlin.text.toDouble
import kotlin.text.toInt
import kotlin.toString

class LiteralNumberEvaluator : Evaluator<Number> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is LiteralNumber -> {
                val value = statement.value.value
                val parsedValue =
                    if (value.toString().contains(".")) {
                        value.toDouble()
                    } else {
                        value.toInt()
                    }
                Result.success(EvaluationResult(parsedValue, context))
            }
            else -> Result.failure(Exception("Not evaluator for: ${statement::class.simpleName}"))
        }
    }
}
