package org.gudelker.evaluator

import org.gudelker.Callable
import org.gudelker.Statement
import org.gudelker.callable.CallableValidator

class CallableEvaluator(private val functions: List<CallableValidator>) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is Callable -> {
                val argumentResult = Analyzer.analyze(statement.expression, context, evaluators)
                when (statement.functionName.value) {
                    "println" -> {
                        println(argumentResult.value)
                        EvaluationResult(Unit, argumentResult.context)
                    }
                    else -> throw UnsupportedOperationException("Función no soportada: ${statement.functionName}")
                }
            }
            else -> throw IllegalArgumentException("Expected Callable, got ${statement::class.simpleName}")
        }
    }
}
