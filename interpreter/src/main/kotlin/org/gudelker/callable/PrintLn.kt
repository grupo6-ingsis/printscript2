package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult
import org.gudelker.expressions.Callable
import org.gudelker.statements.interfaces.Statement

class PrintLn : CallableValidator {
    override fun matches(statement: Statement): Boolean {
        return statement is Callable && statement.functionName.value == "println"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        val something = argumentResult.value.toString()
        println(something)
        return EvaluationResult(something, argumentResult.context) // Devuelve el valor impreso
    }
}
