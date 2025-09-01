package org.gudelker.analyzers

import org.gudelker.Callable
import org.gudelker.Linter
import org.gudelker.LinterAnalyzer
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.validators.IsNotALiteral

class CallableAnalyzer : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Callable
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
    ): LinterResult {
        if (statement is Callable) {
            for ((ruleName, config) in ruleMap) {
                when (ruleName) {
                    "restrictPrintlnExpressions" -> {
                        if (config.restrictPrintlnExpressions) {
                            val arg = statement.expression
                            if (IsNotALiteral().validate(arg)) {
                                return LintViolation("The argument to println must be a literal value.")
                            }
                        }
                    }
                }
            }
            linter.lintNode(statement.expression, ruleMap)
            return ValidLint("Callable statement passed")
        } else {
            throw IllegalArgumentException("Unsupported statement type: ${statement::class.java}")
        }
    }
}
