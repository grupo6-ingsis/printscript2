package org.gudelker

import org.gudelker.evaluator.BinaryEvaluator
import org.gudelker.evaluator.CallableEvaluator
import org.gudelker.evaluator.Evaluator
import org.gudelker.evaluator.GroupingEvaluator
import org.gudelker.evaluator.LiteralIdentifierEvaluator
import org.gudelker.evaluator.LiteralNumberEvaluator
import org.gudelker.evaluator.LiteralStringEvaluator
import org.gudelker.evaluator.UnaryEvaluator
import org.gudelker.evaluator.VariableDeclarationEvaluator
import org.gudelker.evaluator.VariableReassignmentEvaluator
import org.gudelker.utilities.Version

class ListCase {
    fun listForVersion(version: Version): List<Evaluator<out Any>> {
        val evaluators =
            when (version) {
                Version.V1 ->
                    listOf(
                        LiteralNumberEvaluator(),
                        LiteralStringEvaluator(),
                        LiteralIdentifierEvaluator(),
                        UnaryEvaluator(),
                        BinaryEvaluator(),
                        GroupingEvaluator(),
                        VariableDeclarationEvaluator(),
                        VariableReassignmentEvaluator(),
                        CallableEvaluator(),
                    )
                Version.V2 ->
                    listOf(
                        LiteralNumberEvaluator(),
                        LiteralStringEvaluator(),
                        LiteralIdentifierEvaluator(),
                        UnaryEvaluator(),
                        BinaryEvaluator(),
                        GroupingEvaluator(),
                        VariableDeclarationEvaluator(),
                        VariableReassignmentEvaluator(),
                        CallableEvaluator(),
                    )
            }
        return evaluators
    }
}
