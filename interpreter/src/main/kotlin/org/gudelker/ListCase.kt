package org.gudelker

import org.gudelker.callable.ReadInput
import org.gudelker.compare.operators.Equals
import org.gudelker.compare.operators.Greater
import org.gudelker.compare.operators.GreaterEquals
import org.gudelker.compare.operators.Lesser
import org.gudelker.compare.operators.LesserEquals
import org.gudelker.compare.operators.NotEquals
import org.gudelker.evaluator.BinaryEvaluator
import org.gudelker.evaluator.BooleanExpressionEvaluator
import org.gudelker.evaluator.CallableCallEvaluator
import org.gudelker.evaluator.CallableEvaluator
import org.gudelker.evaluator.ConditionalEvaluator
import org.gudelker.evaluator.ConstDeclarationEvaluator
import org.gudelker.evaluator.Evaluator
import org.gudelker.evaluator.GroupingEvaluator
import org.gudelker.evaluator.LiteralBooleanEvaluator
import org.gudelker.evaluator.LiteralIdentifierEvaluator
import org.gudelker.evaluator.LiteralNumberEvaluator
import org.gudelker.evaluator.LiteralStringEvaluator
import org.gudelker.evaluator.UnaryEvaluator
import org.gudelker.evaluator.VariableDeclarationEvaluator
import org.gudelker.evaluator.VariableReassignmentEvaluator
import org.gudelker.inputprovider.TestInputProvider
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.DivisionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.operators.MultiplyOperator
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
                        BinaryEvaluator(
                            setOf(
                                AdditionOperator::class.java,
                                MinusOperator::class.java,
                                MultiplyOperator::class.java,
                                DivisionOperator::class.java,
                            ),
                        ),
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
                        LiteralBooleanEvaluator(),
                        UnaryEvaluator(),
                        BinaryEvaluator(
                            setOf(
                                AdditionOperator::class.java,
                                MinusOperator::class.java,
                                MultiplyOperator::class.java,
                                DivisionOperator::class.java,
                            ),
                        ),
                        BooleanExpressionEvaluator(
                            setOf(
                                Equals::class.java,
                                NotEquals::class.java,
                                Greater::class.java,
                                Lesser::class.java,
                                GreaterEquals::class.java,
                                LesserEquals::class.java,
                            ),
                        ),
                        GroupingEvaluator(),
                        ConstDeclarationEvaluator(),
                        VariableDeclarationEvaluator(),
                        VariableReassignmentEvaluator(),
                        CallableEvaluator(),
                        CallableCallEvaluator(
                            listOf(
                                ReadInput(
                                    TestInputProvider(
                                        mutableListOf(
                                            "hola",
                                        ),
                                    ),
                                ),
                            ),
                        ),
                        ConditionalEvaluator(),
                    )
            }
        return evaluators
    }
}
