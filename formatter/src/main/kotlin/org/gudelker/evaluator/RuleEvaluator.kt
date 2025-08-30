package org.gudelker.evaluator

import org.gudelker.Statement

interface RuleEvaluator {
    fun evaluateRule(statement: Statement): String
}
