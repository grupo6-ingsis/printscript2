package org.gudelker.evaluator

import org.gudelker.Binary
import org.gudelker.Callable
import org.gudelker.ExpressionStatement
import org.gudelker.Grouping
import org.gudelker.LiteralIdentifier
import org.gudelker.LiteralNumber
import org.gudelker.LiteralString
import org.gudelker.Statement
import org.gudelker.Unary

object Analyzer {
    val binaryEvaluators: List<Evaluator<Any>> =
        listOf(
            LiteralNumberEvaluator(),
            LiteralStringEvaluator(),
            LiteralIdentifierEvaluator(),
            UnaryEvaluator(),
            CallableEvaluator(),
            GroupingEvaluator(),
        ) as List<Evaluator<Any>>

    fun analyze(statement: Statement): Any {
        when (statement) {
            is ExpressionStatement -> {
                return when (statement) {
                    is LiteralNumber -> {
                        LiteralNumberEvaluator().evaluate(statement)
                    }

                    is LiteralString -> {
                        LiteralStringEvaluator().evaluate(statement)
                    }

                    is LiteralIdentifier -> {
                        LiteralIdentifierEvaluator().evaluate(statement)
                    }

                    is Unary -> {
                        UnaryEvaluator().evaluate(statement)
                    }

                    is Binary -> {
                        BinaryEvaluator(binaryEvaluators).evaluate(statement)
                    }

                    is Callable -> {
                        CallableEvaluator().evaluate(statement)
                    }

                    is Grouping -> {
                        GroupingEvaluator().evaluate(statement)
                    }
                }
            }
            else -> throw IllegalArgumentException("Expected ExpressionStatement, got ${statement::class.simpleName}")
        }
    }
}
