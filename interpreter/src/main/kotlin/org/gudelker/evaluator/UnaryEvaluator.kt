package org.gudelker.evaluator

import org.gudelker.expressions.Unary
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.statements.interfaces.Statement

class UnaryEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is Unary -> {
                Analyzer.analyze(statement.value, context, evaluators).fold(
                    onSuccess = { valueEvalResult ->
                        when (statement.operator) {
                            is AdditionOperator ->
                                performUnaryAddition(valueEvalResult.value!!)
                                    .map { EvaluationResult(it, valueEvalResult.context) }
                            is MinusOperator ->
                                performUnaryMinus(valueEvalResult.value!!)
                                    .map { EvaluationResult(it, valueEvalResult.context) }
                            else -> Result.failure(UnsupportedOperationException("Operador unario no soportado: ${statement.operator}"))
                        }
                    },
                    onFailure = { Result.failure(it) },
                )
            }
            else -> Result.failure(IllegalArgumentException("Expected Unary, got ${statement::class.simpleName}"))
        }
    }

    private fun performUnaryAddition(value: Any): Result<Any> {
        return when (value) {
            is Number -> Result.success(+value.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipo incompatible para operador unario +"))
        }
    }

    private fun performUnaryMinus(value: Any): Result<Any> {
        return when (value) {
            is Number -> Result.success(-value.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipo incompatible para operador unario -"))
        }
    }
}
