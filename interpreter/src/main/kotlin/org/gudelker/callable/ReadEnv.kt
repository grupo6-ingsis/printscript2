package org.gudelker.callable

import org.gudelker.evaluator.EvaluationResult
import org.gudelker.expressions.CallableCall
import org.gudelker.statements.interfaces.Statement
import kotlin.toString

class ReadEnv : CallableValidator {
    override fun matches(statement: Statement): Boolean {
        return statement is CallableCall && statement.functionName.value == "readEnv"
    }

    override fun execute(argumentResult: EvaluationResult): EvaluationResult {
        val envVarName = argumentResult.value.toString()
        val result = System.getenv(envVarName) ?: throw IllegalArgumentException("Variable de entorno '$envVarName' no encontrada")
        return EvaluationResult(result, argumentResult.context)
    }
}
