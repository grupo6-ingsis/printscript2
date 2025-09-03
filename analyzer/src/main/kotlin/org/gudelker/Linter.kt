package org.gudelker

import org.gudelker.result.CompoundResult
import org.gudelker.result.LinterResult

interface Linter {
    fun lint(
        statementStream: StatementStream,
        ruleMap: Map<String, LinterConfig>,
    ): CompoundResult

    fun lintNode(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        results: List<LinterResult>,
    ): List<LinterResult>
}
