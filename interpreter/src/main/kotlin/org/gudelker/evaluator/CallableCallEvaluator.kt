package org.gudelker.evaluator

import org.gudelker.CallableCall
import org.gudelker.Statement
import org.gudelker.callable.CallableValidator

class CallableCallEvaluator(private val callables: List<CallableValidator>) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        if (statement is CallableCall) {
            for (callable in callables) {
                if (callable.matches(statement)) {
                    val argumentResult = Analyzer.analyze(statement.expression, context, evaluators)
                    val result = callable.execute(argumentResult)
                    return result
                }
            }
        }
        throw IllegalArgumentException("Invalid callable statement")
    }
}
