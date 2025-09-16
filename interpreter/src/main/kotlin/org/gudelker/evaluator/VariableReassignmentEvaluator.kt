package org.gudelker.evaluator

import org.gudelker.statements.VariableReassignment
import org.gudelker.statements.interfaces.Statement
import org.gudelker.types.TypeValidator

class VariableReassignmentEvaluator(
    private val acceptedTypes: Map<String, TypeValidator>,
) : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return try {
            when (statement) {
                is VariableReassignment -> {
                    val name = statement.identifier.value
                    if (!context.hasVariable(name)) {
                        return Result.failure(
                            Exception("Variable '$name' no declarada"),
                        )
                    }
                    val expectedType = context.getVariableType(name)
                    val valueResult = Analyzer.analyze(statement.value, context, evaluators)
                    val value = valueResult.getOrThrow().value

                    if (expectedType != null) {
                        val validator = acceptedTypes[expectedType]
                        if (validator == null) {
                            return Result.failure(
                                Exception("Tipo '$expectedType' no soportado"),
                            )
                        }
                        if (value != null && !validator.isInstance(value)) {
                            return Result.failure(
                                Exception(
                                    "Tipo de dato inválido para variable '$name': " +
                                        "se esperaba '$expectedType', pero se obtuvo '${value::class.simpleName}'",
                                ),
                            )
                        }
                    }
                    val newContext = valueResult.getOrThrow().context.updateVariable(name, value!!)
                    Result.success(EvaluationResult(Unit, newContext))
                }
                else ->
                    Result.failure(
                        Exception("Expected VariableReassignment, got ${statement::class.simpleName}"),
                    )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
