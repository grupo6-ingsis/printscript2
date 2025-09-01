package org.gudelker.analyzers

import org.gudelker.Binary
import org.gudelker.Linter
import org.gudelker.LinterAnalyzer
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint

class BinaryExpressionAnalyzer : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Binary
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
    ): LinterResult {
        if (statement is Binary) {
            for ((ruleName, config) in ruleMap) {
                when (ruleName) {
                }
            }
            linter.lintNode(statement.leftExpression, ruleMap)
            linter.lintNode(statement.rightExpression, ruleMap)
            return ValidLint("Binary expression passed")
        } else {
            throw IllegalArgumentException("Unsupported statement type: ${statement::class.java}")
        }
    }
}
