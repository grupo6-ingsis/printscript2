package org.gudelker.evaluator

import org.gudelker.Statement
import org.gudelker.VariableDeclaration

class VariableDeclarationEvaluator : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is VariableDeclaration -> {
                val name = statement.identifierCombo.value
                if (context.hasConstant(name)) {
                    throw IllegalArgumentException("No se puede declarar variable '$name': ya existe como constante")
                }
                val valueResult = Analyzer.analyze(statement.value, context, evaluators)
                if (valueResult.value != null) {
                    val expectedType = statement.type
                    val actualType = mapRuntimeTypeToLangType(valueResult.value)
                    if (expectedType != null && expectedType != actualType) {
                        throw IllegalArgumentException(
                            "Tipo de dato inválido para variable '$name': se esperaba '$expectedType', pero se obtuvo '$actualType'",
                        )
                    }
                }
                val newContext = valueResult.context.setVariable(name, valueResult.value)
                EvaluationResult(Unit, newContext)
            }
            else -> throw IllegalArgumentException("Expected VariableDeclaration, got ${statement::class.simpleName}")
        }
    }

    private fun mapRuntimeTypeToLangType(value: Any?): String? =
        when (value) {
            is String -> "String"
            is Int -> "Number"
            is Double -> "Number"
            is Boolean -> "boolean"
            null -> null
            else -> "Unknown"
        }
}
