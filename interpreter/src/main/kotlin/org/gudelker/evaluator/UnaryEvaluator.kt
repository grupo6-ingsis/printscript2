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
    ): EvaluationResult {
        return when (statement) {
            is Unary -> {
                val valueResult = Analyzer.analyze(statement.value, context, evaluators)
                val result =
                    when (statement.operator) {
                        is AdditionOperator -> performUnaryAddition(valueResult.value)
                        is MinusOperator -> performUnaryMinus(valueResult.value)
                        else -> throw UnsupportedOperationException("Operador unario no soportado: ${statement.operator}")
                    }
                EvaluationResult(result, valueResult.context)
            }
            else -> throw IllegalArgumentException("Expected Unary, got ${statement::class.simpleName}")
        }
    }

    private fun performUnaryAddition(value: Any): Any {
        return when (value) {
            is Number -> +value.toDouble()
            else -> throw IllegalArgumentException("Tipo incompatible para operador unario +")
        }
    }

    private fun performUnaryMinus(value: Any): Any {
        return when (value) {
            is Number -> -value.toDouble()
            else -> throw IllegalArgumentException("Tipo incompatible para operador unario -")
        }
    }
}
