package org.gudelker.callable

import org.gudelker.CallableCall
import org.gudelker.Statement
import org.gudelker.evaluator.EvaluationResult
import org.gudelker.inputprovider.InputProvider

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
