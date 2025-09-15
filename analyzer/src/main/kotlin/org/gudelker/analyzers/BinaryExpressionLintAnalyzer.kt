package org.gudelker.analyzers

import org.gudelker.expressions.Binary
import org.gudelker.linter.Linter
import org.gudelker.linter.LinterConfig
import org.gudelker.result.LinterResult
import org.gudelker.rulelinter.RuleLinter
import org.gudelker.statements.interfaces.Statement

class BinaryExpressionLintAnalyzer(private val linterRules: List<RuleLinter>) : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Binary
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult> {
        if (statement is Binary) {
            val newList =
                linterRules.fold(results) { acc, rule ->
                    if (rule.matches(ruleMap)) acc + rule.validate(statement) else acc
                }
            val leftResults = linter.lintNode(statement.leftExpression, ruleMap, newList)
            val rightResults = linter.lintNode(statement.rightExpression, ruleMap, leftResults)
            return rightResults
        }
        return results
    }
}
