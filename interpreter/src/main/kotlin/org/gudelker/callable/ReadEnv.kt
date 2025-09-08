package org.gudelker.callable

import org.gudelker.CallableCall
import org.gudelker.Statement
import org.gudelker.evaluator.EvaluationResult

class ReadEnv : CallableValidator {
    override fun matches(statement: Statement): Boolean {
        return statement is CallableCall && statement.functionName.value == "readEnv"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        TODO("Not yet implemented")
    }
}
