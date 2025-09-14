package org.gudelker.evaluator

import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.statements.interfaces.Statement
import org.gudelker.types.TypeValidator

class VariableDeclarationEvaluator(
    private val acceptedTypes: Map<String, TypeValidator>,
) : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return try {
            when (statement) {
                is VariableDeclaration -> {
                    val name = statement.identifierCombo.value
                    if (context.hasVariable(name) || context.hasConstant(name)) {
                        return Result.failure(
                            IllegalArgumentException("Variable o constante '$name' ya declarada"),
                        )
                    }
                    val expectedType = statement.type?.value?.lowercase()
                    val hasValue = statement.value != null
                    val valueResult =
                        if (statement.value != null) {
                            Analyzer.analyze(statement.value!!, context, evaluators)
                        } else {
                            Result.success(EvaluationResult(null, context))
                        }
                    val value: Any? = valueResult.getOrThrow().value

                    if (expectedType != null) {
                        val validator = acceptedTypes[expectedType]
                        if (validator == null) {
                            return Result.failure(
                                IllegalArgumentException("Tipo '$expectedType' no soportado"),
                            )
                        }
                        if (hasValue && value != null && !validator.isInstance(value)) {
                            return Result.failure(
                                IllegalArgumentException(
                                    "Tipo de dato inválido para variable '$name': " +
                                        "se esperaba '$expectedType', pero se obtuvo '${value::class.simpleName}'",
                                ),
                            )
                        }
                        // Guarda el tipo aunque no tenga valor
                        val newContext =
                            if (hasValue) {
                                context.setVariableWithType(name, value!!, expectedType)
                            } else {
                                context.setVariableWithType(name, null, expectedType)
                            }
                        Result.success(EvaluationResult(Unit, newContext))
                    } else {
                        // Si no hay tipo, solo permite si hay valor
                        if (!hasValue) {
                            return Result.failure(
                                IllegalArgumentException("Debe especificar un tipo o un valor para la variable '$name'"),
                            )
                        }
                        // Inferencia extensible usando acceptedTypes
                        val inferredType = acceptedTypes.entries.firstOrNull { it.value.isInstance(value!!) }?.key
                        if (inferredType == null) {
                            return Result.failure(
                                IllegalArgumentException("No se pudo inferir el tipo de la variable '$name'"),
                            )
                        }
                        val newContext = context.setVariableWithType(name, value!!, inferredType)
                        Result.success(EvaluationResult(Unit, newContext))
                    }
                }
                else ->
                    Result.failure(
                        IllegalArgumentException("Expected VariableDeclaration, got ${statement::class.simpleName}"),
                    )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
