package org.gudelker

import org.gudelker.analyzers.LinterAnalyzer
import org.gudelker.linterloader.LinterConfigLoader
import org.gudelker.result.CompoundResult
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.statements.interfaces.Statement

class DefaultLinter(private val astLinters: List<LinterAnalyzer>, val configLoader: LinterConfigLoader) : Linter {
    override fun lint(
        statementStream: StatementStream,
        ruleMap: Map<String, LinterConfig>,
    ): CompoundResult {
        var linterResults: List<LintViolation> = emptyList()
        var stream = statementStream

        while (!stream.isAtEnd()) {
            val (statement, nextStream) = stream.next()
            if (statement != null) {
                val result = lintNode(statement, ruleMap, emptyList())
                linterResults = linterResults + result.filterIsInstance<LintViolation>()
            }
            stream = nextStream
        }

        if (linterResults.isNotEmpty()) {
            val compoundResult = CompoundResult(linterResults, "You have some linter errors")
            return compoundResult
        }
        return CompoundResult(listOf(), "All statements passed")
    }

    override fun lintNode(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        results: List<LinterResult>,
    ): List<LinterResult> {
        val linter =
            astLinters.firstOrNull { it.canHandle(statement) }
                ?: throw IllegalArgumentException("No analyzer found for ${statement::class.simpleName}")

        return linter.lint(statement, ruleMap, this, results)
    }
}
