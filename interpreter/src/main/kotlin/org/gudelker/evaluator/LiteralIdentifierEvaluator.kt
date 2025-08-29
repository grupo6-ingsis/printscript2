package org.gudelker.evaluator

import org.gudelker.LiteralIdentifier
import org.gudelker.Statement

class LiteralIdentifierEvaluator : Evaluator<Any> {
    override fun evaluate(statement: Statement): Any {
        when (statement) {
            is LiteralIdentifier -> return statement.value
            else -> throw IllegalArgumentException("Expected LiteralNumber, got ${statement::class.simpleName}")
        }
    }
}
