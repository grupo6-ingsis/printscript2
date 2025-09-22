package org.gudelker.rulelinter
import org.gudelker.expressions.Callable
import org.gudelker.linter.LinterConfig
import org.gudelker.result.LintViolation
import org.gudelker.result.LinterResult
import org.gudelker.result.ValidLint
import org.gudelker.statements.interfaces.Statement
import kotlin.reflect.KClass

class RestrictPrintlnToIdentifierOrLiteral(
    private val allowedTypes: List<KClass<out Statement>>,
) : RuleLinter {
    override fun matches(ruleMap: Map<String, LinterConfig>): Boolean {
        return ruleMap.values.any { it.restrictPrintlnToIdentifierOrLiteral }
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
