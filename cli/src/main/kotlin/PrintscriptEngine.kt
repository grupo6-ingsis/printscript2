package org.example

import org.gudelker.result.InterpreterResult
import org.gudelker.result.ValidInterpretResult

class PrintscriptEngine {
    fun execute(sourceCode: String): InterpreterResult {

        return ValidInterpretResult(listOf(Unit))
    }
}