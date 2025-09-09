package org.gudelker.analyzers
import org.gudelker.Linter
import org.gudelker.LinterConfig
import org.gudelker.expressions.Grouping
import org.gudelker.result.LinterResult
import org.gudelker.rulelinter.RuleLinter
import org.gudelker.statements.interfaces.Statement

class GroupingExpressionLintAnalyzer(private val linterRules: List<RuleLinter>) : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Grouping
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult> {
        if (statement is Grouping) {
            val newList =
                linterRules.fold(results) { acc, rule ->
                    if (rule.matches(ruleMap)) acc + rule.validate(statement) else acc
                }
            return statement.expression?.let {
                linter.lintNode(it, ruleMap, newList)
            } ?: newList
        }
        return results
    }
}
