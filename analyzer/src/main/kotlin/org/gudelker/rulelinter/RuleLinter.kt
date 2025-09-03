package org.gudelker.rulelinter

import org.gudelker.LinterConfig
import org.gudelker.Statement
import org.gudelker.result.LinterResult

interface RuleLinter {
    fun matches(ruleMap: Map<String, LinterConfig>): Boolean

    fun validate(statement: Statement): LinterResult
}
