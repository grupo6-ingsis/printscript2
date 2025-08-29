package org.gudelker.evaluator

import org.gudelker.Statement

class UnaryEvaluator : Evaluator<Any> {
    override fun evaluate(statement: Statement): Any {
        return statement
    }
}
