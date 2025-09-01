package org.gudelker.analyzers

import org.gudelker.Linter
import org.gudelker.LinterAnalyzer
import org.gudelker.LinterConfig
import org.gudelker.LiteralNumber
import org.gudelker.Statement
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint

class LiteralNumberAnalyzer : LinterAnalyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralNumber
    }

    override fun lint(
        statement: Statement,
        ruleMap: Map<String, LinterConfig>,
        linter: Linter,
    ): LinterResult {
        return ValidLint("Literal number is valid")
    }
}
