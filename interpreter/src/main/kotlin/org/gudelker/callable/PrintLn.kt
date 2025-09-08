package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult

class PrintLn : CallableValidator {
    override fun matches(callableName: String): Boolean {
        return callableName == "println"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        println(argumentResult.value)
        return EvaluationResult(Unit, argumentResult.context)
    }
}
