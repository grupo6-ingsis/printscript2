package org.gudelker.rulelinter

import org.gudelker.LinterConfig
import org.gudelker.result.LinterResult
import org.gudelker.statements.interfaces.Statement

interface RuleLinter {
    fun matches(ruleMap: Map<String, LinterConfig>): Boolean

    fun validate(statement: Statement): LinterResult
}
