package org.gudelker.evaluator

import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.statements.interfaces.Statement
import org.gudelker.types.TypeValidator

class ConstDeclarationEvaluator(
    private val acceptedTypes: Map<String, TypeValidator>,
) : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return try {
            when (statement) {
                is ConstDeclaration -> {
                    val name = statement.identifierCombo.value
                    if (context.hasVariable(name)) {
                        return Result.failure(
                            IllegalArgumentException("No se puede declarar constante '$name': ya existe como variable"),
                        )
                    }
                    if (context.hasConstant(name)) {
                        return Result.failure(
                            IllegalArgumentException("Constante '$name' ya declarada"),
                        )
                    }
                    val valueResult = Analyzer.analyze(statement.value, context, evaluators)
                    val value = valueResult.getOrThrow().value
                    val expectedType = statement.type?.lowercase()
                    if (expectedType != null) {
                        val validator = acceptedTypes[expectedType]
                        if (validator == null) {
                            return Result.failure(
                                IllegalArgumentException("Tipo '$expectedType' no soportado"),
                            )
                        }
                        if (value != null && !validator.isInstance(value)) {
                            return Result.failure(
                                IllegalArgumentException(
                                    "Tipo de dato inválido para variable '$name': " +
                                        "se esperaba '$expectedType', pero se obtuvo '${value::class.simpleName}'",
                                ),
                            )
                        }
                    }
                    val newContext = valueResult.getOrThrow().context.setConstant(name, value!!)
                    Result.success(EvaluationResult(Unit, newContext))
                }
                else ->
                    Result.failure(
                        IllegalArgumentException("Expected ConstDeclaration, got ${statement::class.simpleName}"),
                    )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
