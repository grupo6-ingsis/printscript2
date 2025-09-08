package org.gudelker.evaluator

import org.gudelker.ConstDeclaration
import org.gudelker.Statement
import kotlin.reflect.KClass

class ConstDeclarationEvaluator : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is ConstDeclaration -> {
                val name = statement.identifierCombo.value
                if (context.hasVariable(name)) {
                    throw IllegalArgumentException("No se puede declarar constante '$name': ya existe como variable")
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
                val newContext = valueResult.context.setConstant(name, valueResult.value)
                EvaluationResult(Unit, newContext)
            }
            else -> throw IllegalArgumentException("Expected ConstDeclaration, got ${statement::class.simpleName}")
        }
    }

    private fun mapRuntimeTypeToLangType(value: Any?): String? =
        when (value) {
            is String -> "String"
            is Number -> "Number"
            is Boolean -> "boolean"
            null -> null
            else -> "Unknown"
        }

    private fun <T> valueType(variable: T): KClass<*> {
        return variable!!::class
    }
}
