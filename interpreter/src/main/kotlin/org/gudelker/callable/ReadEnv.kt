package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult
import org.gudelker.expressions.CallableCall
import org.gudelker.statements.interfaces.Statement

class ReadEnv : CallableValidator {
    override fun matches(statement: Statement): Boolean {
        return statement is CallableCall && statement.functionName.value == "readEnv"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        TODO("Not yet implemented")
    }
}
