package org.gudelker.analyzers
import org.gudelker.Linter
import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.result.LinterResult

interface LinterAnalyzer {
    fun canHandle(statement: Statement): Boolean

    fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult>
}
