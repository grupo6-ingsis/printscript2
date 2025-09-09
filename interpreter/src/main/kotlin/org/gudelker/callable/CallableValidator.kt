package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult
import org.gudelker.statements.interfaces.Statement

interface CallableValidator {
    fun matches(statement: Statement): Boolean

    fun execute(argumentResult: EvaluationResult): EvaluationResult
}
