package org.gudelker.evaluator

import org.gudelker.Statement
import org.gudelker.Unary
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.MinusOperator

class UnaryEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        return when (statement) {
            is Unary -> {
                val valueResult = Analyzer.analyze(statement.value, context)
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
