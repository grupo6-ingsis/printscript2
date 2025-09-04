package org.gudelker.analyzers

import org.gudelker.Linter
import org.gudelker.LinterConfig
import org.gudelker.LiteralString
import org.gudelker.Statement
import org.gudelker.result.LinterResult
import org.gudelker.rulelinter.RuleLinter

class LiteralStringLintAnalyzer(private val linterRules: List<RuleLinter>) : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralString
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult> {
        if (statement is LiteralString) {
            val newList =
                linterRules.fold(results) { acc, rule ->
                    if (rule.matches(ruleMap)) acc + rule.validate(statement) else acc
                }
            return newList
        }
        return results
    }
}
