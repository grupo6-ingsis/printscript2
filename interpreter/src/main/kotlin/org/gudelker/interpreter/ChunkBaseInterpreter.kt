package org.gudelker.interpreter

import org.gudelker.evaluator.Analyzer
import org.gudelker.evaluator.ConstVariableContext
import org.gudelker.evaluator.Evaluator
import org.gudelker.result.InterpreterResult
import org.gudelker.result.InvalidInterpreterResult
import org.gudelker.result.ValidInterpretResult
import org.gudelker.statements.interfaces.Statement

class ChunkBaseInterpreter(
    private val evaluators: List<Evaluator<out Any>>,
    private val chunkSize: Int = 1000,
    // Procesar de a 1000 statements
) : SecondInterpreter {
    override fun interpret(statements: List<Statement>): InterpreterResult {
        var context = ConstVariableContext()
        val allResults = mutableListOf<Any?>()

        statements.chunked(chunkSize).forEach { chunk ->
            val chunkResult = processChunk(chunk, context, evaluators)

            chunkResult.fold(
                onSuccess = { (results, newContext) ->
                    allResults.addAll(results)
                    context = newContext
                },
                onFailure = { return InvalidInterpreterResult(it) },
            )

            // Sugerir GC después de cada chunk
            System.gc()
        }

        return ValidInterpretResult(allResults)
    }

    private fun processChunk(
        chunk: List<Statement>,
        initialContext: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<Pair<List<Any?>, ConstVariableContext>> {
        var context = initialContext
        val results = mutableListOf<Any?>()

        for (statement in chunk) {
            val result = Analyzer.analyze(statement, context, evaluators)

            if (result.isSuccess) {
                val evalResult = result.getOrThrow()
                context = evalResult.context
                results.add(evalResult.value)
            } else {
                return Result.failure(Exception("Invalid statement"))
            }
        }

        return Result.success(results to context)
    }
}
