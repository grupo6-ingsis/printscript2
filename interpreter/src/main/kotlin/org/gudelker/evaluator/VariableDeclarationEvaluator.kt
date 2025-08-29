package org.gudelker.evaluator

import org.gudelker.Statement
import org.gudelker.VariableDeclaration

class VariableDeclarationEvaluator : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        return when (statement) {
            is VariableDeclaration -> {
                val valueResult = Analyzer.analyze(statement.value, context)
                val newContext = valueResult.context.setVariable(statement.identifier, valueResult.value)
                EvaluationResult(Unit, newContext)
            }
            else -> throw IllegalArgumentException("Expected VariableDeclaration, got ${statement::class.simpleName}")
        }
    }
}
