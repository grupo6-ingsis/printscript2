package org.gudelker.evaluator

import org.gudelker.Statement
import org.gudelker.VariableReassignment

class VariableReassignmentEvaluator : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        return when (statement) {
            is VariableReassignment -> {
                val valueResult = Analyzer.analyze(statement.value, context)
                val newContext = valueResult.context.updateVariable(statement.identifier.value, valueResult.value)
                EvaluationResult(Unit, newContext)
            }
            else -> throw IllegalArgumentException("Expected VariableReassignment, got ${statement::class.simpleName}")
        }
    }
}
