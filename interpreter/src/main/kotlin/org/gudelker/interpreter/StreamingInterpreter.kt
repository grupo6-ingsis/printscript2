package org.gudelker.interpreter

import org.gudelker.evaluator.Analyzer
import org.gudelker.evaluator.ConstVariableContext
import org.gudelker.evaluator.Evaluator
import org.gudelker.statements.interfaces.Statement

sealed class StreamingInterpreterResult {
    data class StatementEvaluated(val result: Any?, val context: ConstVariableContext) : StreamingInterpreterResult()
    data class Error(val message: String) : StreamingInterpreterResult()
    object Finished : StreamingInterpreterResult()
}

class StreamingInterpreter(
    private val evaluators: List<Evaluator<out Any>>
) {
    private var context = ConstVariableContext()
    private val results = mutableListOf<Any?>()
    private var isFinished = false
    private var hasError = false
    private var errorMessage = ""

    fun processStatement(statement: Statement): StreamingInterpreterResult {
        if (hasError) {
            return StreamingInterpreterResult.Error(errorMessage)
        }

        if (isFinished) {
            return StreamingInterpreterResult.Finished
        }

        try {
            val result = Analyzer.analyze(statement, context, evaluators)

            if (result.isSuccess) {
                val evalResult = result.getOrThrow()
                context = evalResult.context
                val value = evalResult.value
                results.add(value)

                return StreamingInterpreterResult.StatementEvaluated(value, context)
            } else {
                hasError = true
                errorMessage = "Failed to analyze statement: ${result.exceptionOrNull()?.message}"
                return StreamingInterpreterResult.Error(errorMessage)
            }

        } catch (e: Exception) {
            hasError = true
            errorMessage = "Interpreter error: ${e.message}"
            return StreamingInterpreterResult.Error(errorMessage)
        }
    }

    fun finish(): List<Any?> {
        isFinished = true
        return results.toList()
    }

    fun getCurrentContext(): ConstVariableContext = context
    fun getResults(): List<Any?> = results.toList()
    fun hasError(): Boolean = hasError
    fun getError(): String? = if (hasError) errorMessage else null
}