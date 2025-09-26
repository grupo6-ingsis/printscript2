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
        return when (statement) {
            is VariableReassignment -> evaluateReassignment(statement, context, evaluators)
            else -> Result.failure(unsupportedStatementError(statement))
        }
    }

    private fun evaluateReassignment(
        statement: VariableReassignment,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        val name = statement.identifier.value
        checkVariableExists(name, context)?.let { return Result.failure(it) }

        val expectedType = context.getVariableType(name)
        val valueResult = evaluateValue(statement, context, evaluators)
        val value = valueResult.getOrElse { return Result.failure(it) }.value

        validateType(expectedType, value, name)?.let { return Result.failure(it) }

        val newContext = valueResult.getOrThrow().context.updateVariable(name, value!!)
        return Result.success(EvaluationResult(Unit, newContext))
    }

    private fun checkVariableExists(
        name: String,
        context: ConstVariableContext,
    ): Exception? {
        if (!context.hasVariable(name)) {
            return Exception("Variable '$name' no declarada")
        }
        return null
    }

    private fun evaluateValue(
        statement: VariableReassignment,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return Analyzer.analyze(statement.value, context, evaluators)
    }

    private fun validateType(
        expectedType: String?,
        value: Any?,
        name: String,
    ): Exception? {
        if (expectedType != null) {
            val validator = acceptedTypes[expectedType]
            if (validator == null) {
                return Exception("Tipo '$expectedType' no soportado")
            }
            if (value != null && !validator.isInstance(value)) {
                return Exception(
                    "Tipo de dato inválido para variable '$name': " +
                        "se esperaba '$expectedType', pero se obtuvo '${value::class.simpleName}'",
                )
            }
        }
        return null
    }

    private fun unsupportedStatementError(statement: Statement) = Exception("Not evaluator for: ${statement::class.simpleName}")
}
