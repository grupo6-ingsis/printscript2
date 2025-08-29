package org.gudelker.evaluator

import org.gudelker.LiteralNumber
import org.gudelker.Statement

class LiteralNumberEvaluator : Evaluator<Number> {
    override fun evaluate(statement: Statement): Number {
        when (statement) {
            is LiteralNumber -> return statement.value
            else -> throw IllegalArgumentException("Expected LiteralNumber, got ${statement::class.simpleName}")
        }
    }
}
