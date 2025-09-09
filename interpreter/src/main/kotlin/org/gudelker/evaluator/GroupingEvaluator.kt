package org.gudelker.evaluator

import org.gudelker.expressions.Grouping
import org.gudelker.statements.interfaces.Statement

class GroupingEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): EvaluationResult {
        return when (statement) {
            is Grouping -> {
                if (statement.expression != null) {
                    Analyzer.analyze(statement.expression!!, context, evaluators)
                } else {
                    EvaluationResult(Unit, context)
                }
            }
            else -> throw IllegalArgumentException("Expected Grouping, got ${statement::class.simpleName}")
        }
    }
}
