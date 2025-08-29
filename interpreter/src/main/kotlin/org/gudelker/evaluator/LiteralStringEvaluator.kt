package org.gudelker.evaluator

import org.gudelker.LiteralString
import org.gudelker.Statement

class LiteralStringEvaluator : Evaluator<String> {
    override fun evaluate(statement: Statement): String {
        when (statement) {
            is LiteralString -> return statement.value
            else -> throw IllegalArgumentException("Expected LiteralString, got ${statement::class.simpleName}")
        }
    }
}
