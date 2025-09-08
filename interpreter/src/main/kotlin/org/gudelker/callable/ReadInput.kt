package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult

class ReadInput : CallableValidator {
    override fun matches(callableName: String): Boolean {
        return callableName == "readInput"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        print("Enter input: ")
        val input = readLine() ?: ""
        return EvaluationResult(input, argumentResult.context)
    }
}
