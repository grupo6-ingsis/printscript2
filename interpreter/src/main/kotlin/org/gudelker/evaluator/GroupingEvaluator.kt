package org.gudelker.evaluator

import org.gudelker.expressions.Grouping
import org.gudelker.statements.interfaces.Statement

class GroupingEvaluator : Evaluator<Any> {
    override fun evaluate(
        statement: Statement,
        context: ConstVariableContext,
        evaluators: List<Evaluator<out Any>>,
    ): Result<EvaluationResult> {
        return when (statement) {
            is Grouping -> {
                if (statement.expression != null) {
                    Analyzer.analyze(statement.expression!!, context, evaluators)
                } else {
                    Result.success(EvaluationResult(Unit, context))
                }
            }
            else -> Result.failure(IllegalArgumentException("Expected Grouping, got ${statement::class.simpleName}"))
        }
    }
}
