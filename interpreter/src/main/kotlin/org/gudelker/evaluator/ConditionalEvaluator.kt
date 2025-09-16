package org.gudelker.evaluator

import org.gudelker.expressions.ConditionalExpression
import org.gudelker.statements.interfaces.Statement

class ConditionalEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is ConditionalExpression -> {
                // Evaluar la condición
                Analyzer.analyze(statement.condition, context, evaluators).fold(
                    onSuccess = { conditionEval ->
                        val conditionValue = conditionEval.value
                        if (conditionValue !is Boolean) {
                            return Result.failure(
                                Exception(
                                    "La condición debe evaluar a un booleano, pero fue: ${conditionValue?.let { it::class.simpleName }}",
                                ),
                            )
                        }
                        val bodyToExecute =
                            if (conditionValue) {
                                statement.ifBody
                            } else {
                                statement.elseBody ?: emptyList()
                            }
                        // Ejecutar el bloque seleccionado
                        bodyToExecute.fold(
                            Result.success(EvaluationResult(Unit, conditionEval.context)),
                        ) { accResult, stmt ->
                            accResult.fold(
                                onSuccess = { acc ->
                                    Analyzer.analyze(stmt, acc.context, evaluators)
                                },
                                onFailure = { Result.failure(it) },
                            )
                        }
                    },
                    onFailure = { Result.failure(it) },
                )
            }
            else ->
                Result.failure(
                    Exception("Expected ConditionalExpression, got ${statement::class.simpleName}"),
                )
        }
    }
}
