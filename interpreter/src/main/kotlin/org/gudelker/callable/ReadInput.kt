package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult
import org.gudelker.expressions.CallableCall
import org.gudelker.inputprovider.InputProvider
import org.gudelker.statements.interfaces.Statement

class ReadInput(private val inputProvider: InputProvider) : CallableValidator {
    override fun matches(statement: Statement): Boolean {
        return statement is CallableCall && statement.functionName.value == "readInput"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        val prompt = argumentResult.value.toString()
        val result = inputProvider.nextInput(prompt)
        return EvaluationResult(result, argumentResult.context)
    }
}
