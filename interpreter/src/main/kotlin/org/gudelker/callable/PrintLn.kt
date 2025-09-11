package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult
import org.gudelker.expressions.Callable
import org.gudelker.statements.interfaces.Statement

class PrintLn : CallableValidator {
    override fun matches(statement: Statement): Boolean {
        return statement is Callable && statement.functionName.value == "println"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        val envVarName = argumentResult.value.toString()
        val result = println(envVarName)
        return EvaluationResult(result, argumentResult.context)
    }
}
