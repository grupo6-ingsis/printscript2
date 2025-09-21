package org.gudelker.evaluator

import org.gudelker.expressions.Unary
import org.gudelker.operators.UnaryOperator
import org.gudelker.statements.interfaces.Statement

class UnaryEvaluator(
    private val supportedOperators: Set<Class<out UnaryOperator>> = emptySet(),
) : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is Unary -> {
                if (operatorNotSupported(statement)) {
                    return Result.failure(UnsupportedOperationException("Operador no soportado: ${statement.operator::class.simpleName}"))
                }
                Analyzer.analyze(statement.value, context, evaluators).fold(
                    onSuccess = { valueEvalResult ->
                        finalResult(statement, valueEvalResult)
                    },
                    onFailure = { Result.failure(it) },
                )
            }
            else -> Result.failure(Exception("Not evaluator for: ${statement::class.simpleName}"))
        }
    }

    private fun finalResult(
        statement: Unary,
        valueEvalResult: EvaluationResult,
    ): Result<EvaluationResult> {
        val operator = statement.operator.value
        return if (operator is UnaryOperator) {
            operator.performUnaryOperation(valueEvalResult.value)
                .map { EvaluationResult(it, valueEvalResult.context) }
        } else {
            Result.failure(UnsupportedOperationException("Operador unoario no soportado: ${operator::class.simpleName}"))
        }
    }

    private fun operatorNotSupported(statement: Unary): Boolean {
        return supportedOperators.isNotEmpty() &&
            !supportedOperators.contains(statement.operator.value::class.java)
    }
}
