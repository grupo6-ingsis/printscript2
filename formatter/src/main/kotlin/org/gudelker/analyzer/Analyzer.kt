package org.gudelker.analyzer

import org.gudelker.evaluator.RuleEvaluator

interface Analyzer {
    fun isRuleOn(
        name: String,
        value: Any?,
    ): RuleEvaluator?
}
