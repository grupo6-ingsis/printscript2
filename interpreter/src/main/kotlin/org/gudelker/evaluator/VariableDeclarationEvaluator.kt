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
        return when (statement) {
            is VariableDeclaration -> evaluateVariableDeclaration(statement, context, evaluators)
            else -> Result.failure(unsupportedStatementError(statement))
        }
    }

    private fun evaluateVariableDeclaration(
        statement: VariableDeclaration,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        val name = statement.identifierCombo.value
        checkNameConflicts(name, context)?.let { return Result.failure(it) }

        val hasValue = statement.value != null
        val valueResult = evaluateValue(statement, context, evaluators)
        val value = valueResult.getOrElse { return Result.failure(it) }.value

        val expectedType = statement.type?.value?.lowercase()
        if (expectedType != null) {
            validateType(expectedType, value, hasValue, name)?.let { return Result.failure(it) }
            val newContext = setVariableWithType(context, name, value, expectedType, hasValue)
            return Result.success(EvaluationResult(Unit, newContext))
        } else {
            if (!hasValue) {
                return Result.failure(missingTypeOrValueError(name))
            }
            val inferredType =
                inferType(value)
                    ?: return Result.failure(cannotInferTypeError(name))
            val newContext = context.setVariableWithType(name, value!!, inferredType)
            return Result.success(EvaluationResult(Unit, newContext))
        }
    }

    private fun checkNameConflicts(
        name: String,
        context: ConstVariableContext,
    ): Exception? {
        if (context.hasVariable(name) || context.hasConstant(name)) {
            return Exception("Variable o constante '$name' ya declarada")
        }
        return null
    }

    private fun evaluateValue(
        statement: VariableDeclaration,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return if (statement.value != null) {
            Analyzer.analyze(statement.value!!, context, evaluators)
        } else {
            Result.success(EvaluationResult(null, context))
        }
    }

    private fun validateType(
        expectedType: String,
        value: Any?,
        hasValue: Boolean,
        name: String,
    ): Exception? {
        val validator = acceptedTypes[expectedType]
        if (validator == null) {
            return Exception("Tipo '$expectedType' no soportado")
        }
        if (hasValue && value != null && !validator.isInstance(value)) {
            return Exception(
                "Tipo de dato inválido para variable '$name': " +
                    "se esperaba '$expectedType', pero se obtuvo '${value::class.simpleName}'",
            )
        }
        return null
    }

    private fun setVariableWithType(
        context: ConstVariableContext,
        name: String,
        value: Any?,
        type: String,
        hasValue: Boolean,
    ): ConstVariableContext {
        return if (hasValue) {
            context.setVariableWithType(name, value!!, type)
        } else {
            context.setVariableWithType(name, null, type)
        }
    }

    private fun inferType(value: Any?): String? {
        return acceptedTypes.entries.firstOrNull { it.value.isInstance(value!!) }?.key
    }

    private fun missingTypeOrValueError(name: String) = Exception("Debe especificar un tipo o un valor para la variable '$name'")

    private fun cannotInferTypeError(name: String) = Exception("No se pudo inferir el tipo de la variable '$name'")

    private fun unsupportedStatementError(statement: Statement) = Exception("Not evaluator for: ${statement::class.simpleName}")
}
