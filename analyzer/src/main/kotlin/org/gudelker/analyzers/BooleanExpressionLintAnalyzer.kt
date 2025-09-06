package org.gudelker.analyzers

import org.gudelker.BooleanExpression
import org.gudelker.Linter
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.result.LinterResult
import org.gudelker.rulelinter.RuleLinter

class BooleanExpressionLintAnalyzer(private val linterRules: List<RuleLinter>) : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is BooleanExpression
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult> {
        if (statement is BooleanExpression) {
            val newList =
                linterRules.fold(results) { acc, rule ->
                    if (rule.matches(ruleMap)) acc + rule.validate(statement) else acc
                }
            val leftResults = linter.lintNode(statement.left, ruleMap, newList)
            val rightResults = linter.lintNode(statement.right, ruleMap, leftResults)
            return rightResults
        }
        return results
    }
}
