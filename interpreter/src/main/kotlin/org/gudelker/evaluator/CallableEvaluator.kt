package org.gudelker.evaluator

import org.gudelker.callable.CallableValidator
import org.gudelker.expressions.Callable
import org.gudelker.statements.interfaces.Statement

class CallableEvaluator(private val callables: List<CallableValidator>) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        if (statement is Callable) {
            for (callable in callables) {
                if (callable.matches(statement)) {
                    val argumentResult = Analyzer.analyze(statement.expression, context, evaluators)
                    return argumentResult.fold(
                        onSuccess = { leftEvalResult ->
                            Result.success(callable.execute(leftEvalResult))
                        },
                        onFailure = { error ->
                            Result.failure(error)
                        },
                    )
                }
            }
        }
        return Result.failure(Exception("Not evaluator for: ${statement::class.simpleName}"))
    }
}
