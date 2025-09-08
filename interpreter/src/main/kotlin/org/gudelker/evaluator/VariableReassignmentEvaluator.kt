package org.gudelker.evaluator

import org.gudelker.Statement
import org.gudelker.VariableReassignment
import kotlin.reflect.KClass

class VariableReassignmentEvaluator : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is VariableReassignment -> {
                val valueResult = Analyzer.analyze(statement.value, context, evaluators)
                if (valueResult.context.hasVariable(statement.identifier.value)) {
                    val identifier = statement.identifier.value
                    if (valueType(valueResult.value) != valueType(valueResult.context.getVariable(identifier))) {
                        throw IllegalArgumentException(
                            "Type mismatch: cannot assign ${valueType(
                                valueResult.value,
                            ).simpleName} to variable '$identifier' of type ${valueType(
                                valueResult.context.getVariable(identifier),
                            ).simpleName}",
                        )
                    }
                }
                val newContext = valueResult.context.updateVariable(statement.identifier.value, valueResult.value)
                EvaluationResult(Unit, newContext)
            }
            else -> throw IllegalArgumentException("Expected VariableReassignment, got ${statement::class.simpleName}")
        }
    }

    private fun <T> valueType(variable: T): KClass<*> {
        return variable!!::class
    }
}
