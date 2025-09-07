package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult

interface CallableValidator {
    fun matches(callableName: String): Boolean

    fun execute(argumentResult: EvaluationResult): EvaluationResult
}
