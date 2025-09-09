package org.gudelker.evaluator

import org.gudelker.expressions.ConditionalExpression
import org.gudelker.statements.interfaces.Statement

class ConditionalEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is ConditionalExpression -> {
                // Evaluar la condición
                val conditionResult = Analyzer.analyze(statement.condition, context, evaluators)

                // Verificar que el resultado sea un booleano
                val conditionValue =
                    when (conditionResult.value) {
                        is Boolean -> conditionResult.value
                        else -> throw IllegalArgumentException(
                            "La condición debe evaluar a un booleano, pero fue: ${conditionResult.value::class.simpleName}",
                        )
                    }

                // Ejecutar el bloque correspondiente
                val bodyToExecute =
                    if (conditionValue) {
                        statement.ifBody
                    } else {
                        statement.elseBody ?: emptyList()
                    }

                // Evaluar todas las sentencias del bloque seleccionado de forma inmutable
                // El fold acumulativo permite pasar el contexto actualizado entre sentencias
                val finalResult =
                    bodyToExecute.fold(
                        EvaluationResult(Unit, conditionResult.context),
                    ) { acc, stmt ->
                        Analyzer.analyze(stmt, acc.context, evaluators)
                    }

                finalResult
            }
            else -> throw IllegalArgumentException("Expected ConditionalExpression, got ${statement::class.simpleName}")
        }
    }
}
