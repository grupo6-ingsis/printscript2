package org.gudelker.analyzer

import org.gudelker.evaluator.RuleEvaluator
import org.gudelker.evaluator.SpaceBeforeColonEvaluator

class SpaceBeforeColonAnalyzer : Analyzer {
    override fun isRuleOn(
        name: String,
        value: Any?,
    ): RuleEvaluator? {
        if (name == "spaceBeforeColon") {
            return SpaceBeforeColonEvaluator(value)
        }
        return null
    }
}
