package org.gudelker.analyzers
import org.gudelker.Linter
import org.gudelker.LinterAnalyzer
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.VariableDeclaration
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.string.CamelCaseValidator
import org.gudelker.string.SnakeCaseValidator

class VariableDeclarationAnalyzer : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableDeclaration
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
    ): LinterResult {
        if (statement is VariableDeclaration) {
            for ((ruleName, config) in ruleMap) {
                when (ruleName) {
                    "identifierFormat" -> {
                        val identifier = statement.identifier
                        val isValid =
                            when (config.identifierFormat) {
                                "camelCase" -> CamelCaseValidator().validateString(identifier)
                                "snake_case" -> SnakeCaseValidator().validateString(identifier)
                                else -> true
                            }
                        if (!isValid) {
                            return LintViolation("Variable name '$identifier' does not match ${config.identifierFormat} format")
                        }
                    }
                }
            }
            linter.lintNode(statement.value, ruleMap)
            return ValidLint("All variable declaration rules passed")
        } else {
            throw IllegalArgumentException("Unsupported statement type: ${statement::class.java}")
        }
    }
}
