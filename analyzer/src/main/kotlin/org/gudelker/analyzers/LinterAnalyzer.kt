package org.gudelker.analyzers
import org.gudelker.linter.Linter
import org.gudelker.linter.LinterConfig
import org.gudelker.result.LinterResult
import org.gudelker.statements.interfaces.Statement

interface LinterAnalyzer {
    fun canHandle(statement: Statement): Boolean

    fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
        results: List<LinterResult>,
    ): List<LinterResult>
}
