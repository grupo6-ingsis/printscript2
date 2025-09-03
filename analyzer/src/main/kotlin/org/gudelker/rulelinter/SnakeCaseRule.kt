package org.gudelker.rulelinter

import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.VariableDeclaration
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint

class SnakeCaseRule : RuleLinter {
    override fun matches(ruleMap: Map<String, LinterConfig>): Boolean {
        return ruleMap.values.any { it.identifierFormat == "snake_case" }
    }

    override fun validate(statement: Statement): LinterResult {
        if (statement is VariableDeclaration) {
            val snakeCaseRegex = Regex("^[a-z]+(_[a-z]+)*$")
            val isValid = snakeCaseRegex.matches(statement.identifier)
            if (!isValid) {
                return LintViolation("Variable name '${statement.identifier}' does not match snake_case format")
            }
            return ValidLint("Variable name '${statement.identifier}' matches snake_case format")
        }
        return ValidLint("Not a variable declaration")
    }
}
