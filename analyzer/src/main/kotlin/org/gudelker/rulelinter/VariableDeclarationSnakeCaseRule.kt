package org.gudelker.rulelinter

import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.VariableDeclaration
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint

class VariableDeclarationSnakeCaseRule : RuleLinter {
    override fun matches(ruleMap: Map<String, LinterConfig>): Boolean {
        return ruleMap.values.any { it.identifierFormat == "snake_case" }
    }

    override fun validate(statement: Statement): LinterResult {
        if (statement is VariableDeclaration) {
            val snakeCaseRegex = Regex("^[a-z]+(_[a-z]+)*$")
            val isValid = snakeCaseRegex.matches(statement.identifierCombo.value)
            if (!isValid) {
                return LintViolation(
                    "Variable name '${statement.identifierCombo.value}' does not match snake_case format",
                    statement.identifierCombo.position,
                )
            }
            return ValidLint("Variable name '${statement.identifierCombo.value}' matches snake_case format")
        }
        return ValidLint("Not a variable declaration")
    }
}
