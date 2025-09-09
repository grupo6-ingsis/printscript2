package org.gudelker.rulelinter

import org.gudelker.LinterConfig
import org.gudelker.expressions.CallableCall
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.statements.interfaces.Statement
import kotlin.reflect.KClass

class RestrictReadInputExpressions(private val allowedTypes: List<KClass<out Statement>>) : RuleLinter {
    override fun matches(ruleMap: Map<String, LinterConfig>): Boolean {
        val restrictReadInputExpressions = "restrictReadInputExpressions"
        return ruleMap[restrictReadInputExpressions]?.restrictReadInputExpressions == true
    }

    override fun validate(statement: Statement): LinterResult {
        if (statement is CallableCall && statement.functionName.value == "readInput") {
            val arg = statement.expression
            val allowed = allowedTypes.any { it.isInstance(arg) }
            return if (allowed) {
                ValidLint("readInput usage is valid")
            } else {
                LintViolation("Argument not allowed in readInput", statement.functionName.position)
            }
        }
        return ValidLint("Not a readInput statement")
    }
}
