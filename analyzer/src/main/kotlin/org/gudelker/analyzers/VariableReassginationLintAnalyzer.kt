package org.gudelker.analyzers
import org.gudelker.Linter
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.VariableReassignment
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.rulelinter.RuleLinter

class VariableReassginationLintAnalyzer(private val linterRules: List<RuleLinter>) : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableReassignment
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult> {
        if (statement is VariableReassignment) {
            val newList =
                linterRules.fold(results) { acc, rule ->
                    if (rule.matches(ruleMap)) acc + rule.validate(statement) else acc
                }
            return linter.lintNode(statement.value, ruleMap, newList)
        }
        return results + ValidLint("All variable declaration rules passed")
    }
}
