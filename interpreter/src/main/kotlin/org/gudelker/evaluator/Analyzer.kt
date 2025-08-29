package org.gudelker.evaluator

import org.gudelker.Statement

object Analyzer {
    private val evaluators: List<Evaluator<out Any>> =
        listOf(
            LiteralNumberEvaluator(),
            LiteralStringEvaluator(),
            LiteralIdentifierEvaluator(),
            UnaryEvaluator(),
            BinaryEvaluator(getBinaryEvaluators()),
            GroupingEvaluator(),
            VariableDeclarationEvaluator(),
            VariableReassignmentEvaluator(),
            CallableEvaluator(),
        )

    private fun getBinaryEvaluators(): List<Evaluator<out Any>> =
        listOf(
            LiteralNumberEvaluator(),
            LiteralStringEvaluator(),
            LiteralIdentifierEvaluator(),
            UnaryEvaluator(),
            GroupingEvaluator(),
        )

    fun analyze(
        statement: Statement,
        context: VariableContext,
    ): EvaluationResult {
        for (evaluator in evaluators) {
            try {
                return evaluator.evaluate(statement, context)
            } catch (e: IllegalArgumentException) {
                continue
            }
        }
        throw IllegalArgumentException("No se encontró evaluador para: ${statement::class.simpleName}")
    }
}
