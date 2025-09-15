package org.gudelker.rulelinter

import org.gudelker.linter.LinterConfig
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.statements.interfaces.Statement

class ConstDeclarationCamelCaseRule : RuleLinter {
    override fun matches(ruleMap: Map<String, LinterConfig>): Boolean {
        return ruleMap.values.any {
            val normalizedFormat = it.identifierFormat.lowercase().replace(Regex("[^a-z]"), "")
            normalizedFormat == "camelcase"
        }
    }

    override fun validate(statement: Statement): LinterResult {
        val statement = statement as ConstDeclaration
        val camelCaseRegex = "^[a-z]+(?:[A-Z][a-z]*)*$".toRegex()
        val isValid = camelCaseRegex.matches(statement.identifierCombo.value)
        if (!isValid) {
            return LintViolation(
                "LintViolation(\"Variable name '${statement.identifierCombo.value}' does not match camelCase format\")",
                statement.identifierCombo.position,
            )
        }
        return ValidLint("Variable name '${statement.identifierCombo.value}' matches camelCase format")
    }
}
