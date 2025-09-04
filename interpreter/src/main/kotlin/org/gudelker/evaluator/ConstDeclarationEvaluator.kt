package org.gudelker.evaluator

import org.gudelker.ConstDeclaration
import org.gudelker.Statement

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
                val newContext = valueResult.context.setConstant(name, valueResult.value)
                EvaluationResult(Unit, newContext)
            }
            else -> throw IllegalArgumentException("Expected ConstDeclaration, got ${statement::class.simpleName}")
        }
    }
}
