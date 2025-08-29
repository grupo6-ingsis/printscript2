package org.gudelker.evaluator

import org.gudelker.Grouping
import org.gudelker.Statement

class GroupingEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        return when (statement) {
            is Grouping -> {
                if (statement.expression != null) {
                    Analyzer.analyze(statement.expression!!, context)
                } else {
                    EvaluationResult(Unit, context)
                }
            }
            else -> throw IllegalArgumentException("Expected Grouping, got ${statement::class.simpleName}")
        }
    }
}
