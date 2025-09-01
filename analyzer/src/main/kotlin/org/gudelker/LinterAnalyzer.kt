package org.gudelker

import org.gudelker.result.LinterResult

interface LinterAnalyzer {
    fun canHandle(statement: Statement): Boolean

    fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
    ): LinterResult
}
