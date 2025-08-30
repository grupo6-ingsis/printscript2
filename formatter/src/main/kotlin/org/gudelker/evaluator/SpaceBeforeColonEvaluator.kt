package org.gudelker.evaluator

import org.gudelker.Statement
import org.gudelker.VariableDeclaration

class SpaceBeforeColonEvaluator(
    private val on: Any?,
) : RuleEvaluator {
    override fun evaluateRule(statement: Statement): String {
        if (statement is VariableDeclaration) {
            if (on == true) {
                return "let ${statement.identifier} : ${statement.type} = ${statement.value}"
            }
            return "let ${statement.identifier}: ${statement.type} = ${statement.value}"
        }
        return ""
    }
}
