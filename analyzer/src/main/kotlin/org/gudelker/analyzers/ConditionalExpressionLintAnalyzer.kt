package org.gudelker.analyzers

import org.gudelker.Linter
import org.gudelker.LinterConfig
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.result.LinterResult
import org.gudelker.rulelinter.RuleLinter
import org.gudelker.statements.interfaces.Statement

class ConditionalExpressionLintAnalyzer(private val linterRules: List<RuleLinter>) : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is ConditionalExpression
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult> {
        if (statement is ConditionalExpression) {
            val newList =
                linterRules.fold(results) { acc, rule ->
                    if (rule.matches(ruleMap)) acc + rule.validate(statement) else acc
                }
            val conditionResults = linter.lintNode(statement.condition, ruleMap, newList)
            val ifResults =
                statement.ifBody.fold(conditionResults) { acc, stmt ->
                    linter.lintNode(stmt, ruleMap, acc)
                }
            val elseResults =
                statement.elseBody?.fold(ifResults) { acc, stmt ->
                    linter.lintNode(stmt, ruleMap, acc)
                }
            return elseResults ?: ifResults
        }
        return results
    }
}
