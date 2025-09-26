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
        return when (statement) {
            is ConstDeclaration -> evaluateConstDeclaration(statement, context, evaluators)
            else -> Result.failure(unsupportedStatementError(statement))
        }
    }

    private fun evaluateConstDeclaration(
        statement: ConstDeclaration,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        val name = statement.identifierCombo.value
        checkNameConflicts(name, context)?.let { return Result.failure(it) }

        val valueResult = evaluateValue(statement, context, evaluators)
        val value = valueResult.getOrElse { return Result.failure(it) }.value

        validateType(statement, value)?.let { return Result.failure(it) }

        val newContext = valueResult.getOrThrow().context.setConstant(name, value!!)
        return Result.success(EvaluationResult(Unit, newContext))
    }

    private fun checkNameConflicts(
        name: String,
        context: ConstVariableContext,
    ): Exception? {
        if (context.hasVariable(name)) {
            return Exception("No se puede declarar constante '$name': ya existe como variable")
        }
        if (context.hasConstant(name)) {
            return Exception("Constante '$name' ya declarada")
        }
        return null
    }

    private fun evaluateValue(
        statement: ConstDeclaration,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return Analyzer.analyze(statement.value, context, evaluators)
    }

    private fun validateType(
        statement: ConstDeclaration,
        value: Any?,
    ): Exception? {
        val expectedType = statement.type?.value?.lowercase()
        if (expectedType != null) {
            val validator = acceptedTypes[expectedType]
            if (validator == null) {
                return Exception("Tipo '$expectedType' no soportado")
            }
            if (value != null && !validator.isInstance(value)) {
                return Exception(
                    "Tipo de dato inválido para variable '${statement.identifierCombo.value}': " +
                        "se esperaba '$expectedType', pero se obtuvo '${value::class.simpleName}'",
                )
            }
        }
        return null
    }

    private fun unsupportedStatementError(statement: Statement) = Exception("Not evaluator for: ${statement::class.simpleName}")
}
