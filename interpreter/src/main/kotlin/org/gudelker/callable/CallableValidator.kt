package org.gudelker.callable

import org.gudelker.Statement
import org.gudelker.evaluator.EvaluationResult

interface CallableValidator {
    fun matches(statement: Statement): Boolean

    fun execute(argumentResult: EvaluationResult): EvaluationResult
}
