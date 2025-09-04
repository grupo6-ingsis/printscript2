package org.gudelker.evaluator

import org.gudelker.Statement
import org.gudelker.VariableDeclaration

class VariableDeclarationEvaluator : Evaluator<Unit> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is VariableDeclaration -> {
                val valueResult = Analyzer.analyze(statement.value, context, evaluators)
                val newContext = valueResult.context.setVariable(statement.identifierCombo.value, valueResult.value)
                EvaluationResult(Unit, newContext)
            }
            else -> throw IllegalArgumentException("Expected VariableDeclaration, got ${statement::class.simpleName}")
        }
    }
}
