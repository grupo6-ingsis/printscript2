package org.gudelker.rulelinter
import org.gudelker.LinterConfig
import org.gudelker.expressions.Callable
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.statements.interfaces.Statement
import kotlin.reflect.KClass

class RestrictPrintLnExpressions(
    private val allowedTypes: List<KClass<out Statement>>,
) : RuleLinter {
    override fun matches(ruleMap: Map<String, LinterConfig>): Boolean {
        val restrictPrintLnExpressions = "restrictPrintlnExpressions"
        return ruleMap[restrictPrintLnExpressions]?.restrictPrintlnExpressions == true
    }

    override fun validate(statement: Statement): LinterResult {
        if (statement is Callable && statement.functionName.value == "println") {
            val arg = statement.expression
            val allowed = allowedTypes.any { it.isInstance(arg) }
            return if (allowed) {
                ValidLint("println usage is valid")
            } else {
                LintViolation("Argument not allowed in println", statement.functionName.position)
            }
        }
        return ValidLint("Not a println statement")
    }
}
