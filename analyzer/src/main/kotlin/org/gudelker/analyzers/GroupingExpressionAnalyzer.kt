package org.gudelker.analyzers

import org.gudelker.Grouping
import org.gudelker.Linter
import org.gudelker.LinterAnalyzer
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint

class GroupingExpressionAnalyzer : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Grouping
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
    ): LinterResult {
        if (statement is Grouping) {
            for ((ruleName, config) in ruleMap) {
                when (ruleName) {
                }
            }
            statement.expression?.let {
                linter.lintNode(it, ruleMap)
            }
            return ValidLint("Grouping expression passed")
        } else {
            throw IllegalArgumentException("Unsupported statement type: ${statement::class.java}")
        }
    }
}
