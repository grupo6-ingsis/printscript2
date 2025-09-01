package org.gudelker

import org.gudelker.result.CompoundResult
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult

class DefaultLinter(private val linters: List<LinterAnalyzer>, private val linterConfigLoader: LinterConfigLoader) : Linter {
    override fun lint(
        statementStream: StatementStream,
        ruleMap: Map<String, LinterConfig>,
    ): CompoundResult {
        var linterResults: List<LinterResult> = emptyList()
        var stream = statementStream

        while (!stream.isAtEnd()) {
            val (statement, nextStream) = stream.next()
            if (statement != null) {
                val result = lintNode(statement, ruleMap)
                linterResults = linterResults + result
            }
            stream = nextStream
        }
        val linterErrors: List<LintViolation> = linterResults.filter { it is LintViolation } as List<LintViolation>

        if (linterErrors.isNotEmpty()) {
            val compoundResult = CompoundResult(linterErrors, "You have some linter errors")
            return compoundResult
        }
        return CompoundResult(listOf(), "All statements passed")
    }

    override fun lintNode(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
    ): LinterResult {
        val linter =
            linters.firstOrNull { it.canHandle(statement) }
                ?: throw IllegalArgumentException("No analyzer found for ${statement::class.simpleName}")

        return linter.lint(statement, ruleMap, this)
    }
}
