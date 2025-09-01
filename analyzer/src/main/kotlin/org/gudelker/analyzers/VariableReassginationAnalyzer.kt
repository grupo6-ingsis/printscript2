package org.gudelker.analyzers

import org.gudelker.Linter
import org.gudelker.LinterAnalyzer
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.VariableReassignment
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint

class VariableReassginationAnalyzer : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableReassignment
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
    ): LinterResult {
        if (statement is VariableReassignment) {
            for ((ruleName, config) in ruleMap) {
                when (ruleName) {
                }
            }
            linter.lintNode(statement.value, ruleMap)
            return ValidLint("Variable reassignment passed")
        } else {
            throw IllegalArgumentException("Unsupported statement type: ${statement::class.java}")
        }
    }
}
