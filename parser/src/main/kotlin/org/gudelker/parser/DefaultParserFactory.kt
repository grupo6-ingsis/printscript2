package org.gudelker.parser

import org.gudelker.compare.operators.Equals
import org.gudelker.compare.operators.Greater
import org.gudelker.compare.operators.GreaterEquals
import org.gudelker.compare.operators.Lesser
import org.gudelker.compare.operators.LesserEquals
import org.gudelker.compare.operators.NotEquals
import org.gudelker.operators.AdditionOperator
import org.gudelker.operators.DivisionOperator
import org.gudelker.operators.MinusOperator
import org.gudelker.operators.MultiplyOperator
import org.gudelker.rule.BinaryParRule
import org.gudelker.rule.BooleanExpressionParRule
import org.gudelker.rule.CallableCallParRule
import org.gudelker.rule.CallableParRule
import org.gudelker.rule.ConditionalParRule
import org.gudelker.rule.ConstDeclarationParRule
import org.gudelker.rule.ExpressionParRule
import org.gudelker.rule.GroupingParRule
import org.gudelker.rule.LiteralBooleanParRule
import org.gudelker.rule.LiteralIdentifierParRule
import org.gudelker.rule.LiteralNumberParRule
import org.gudelker.rule.LiteralStringParRule
import org.gudelker.rule.UnaryParRule
import org.gudelker.rule.VariableDeclarationParRule
import org.gudelker.rule.VariableReassignmentParRule
import org.gudelker.utilities.Version

object DefaultParserFactory {
    fun createParser(version: Version): DefaultParser {
        return when (version) {
            Version.V1 -> createParserV1()
            Version.V2 -> createParserV2()
        }
    }

    private fun createParserV1(): DefaultParser {
        val literalNumberParRule = LiteralNumberParRule()
        val literalStringParRule = LiteralStringParRule()
        val literalIdentifierParRule = LiteralIdentifierParRule()

        val baseExpressionParRule = ExpressionParRule(listOf(literalNumberParRule, literalStringParRule, literalIdentifierParRule))
        val unaryParRule = UnaryParRule(baseExpressionParRule)
        val groupingParRule = GroupingParRule(baseExpressionParRule)

        val binaryExpressionParRule =
            ExpressionParRule(listOf(unaryParRule, groupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule))

        val binaryParRule =
            BinaryParRule(
                binaryExpressionParRule,
                additionOperators =
                    mapOf(
                        "+" to { AdditionOperator() },
                        "-" to { MinusOperator() },
                    ),
                multiplicationOperators =
                    mapOf(
                        "*" to { MultiplyOperator() },
                        "/" to { DivisionOperator() },
                    ),
            )

        val fullExpressionParRule =
            ExpressionParRule(
                listOf(
                    binaryParRule,
                    unaryParRule,
                    groupingParRule,
                    literalNumberParRule,
                    literalStringParRule,
                    literalIdentifierParRule,
                ),
            )

        val finalGroupingParRule = GroupingParRule(fullExpressionParRule)

        val finalBinaryExpressionParRule =
            ExpressionParRule(
                listOf(unaryParRule, finalGroupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule),
            )

        val finalBinaryParRule =
            BinaryParRule(
                finalBinaryExpressionParRule,
                additionOperators =
                    mapOf(
                        "+" to { AdditionOperator() },
                        "-" to { MinusOperator() },
                    ),
                multiplicationOperators =
                    mapOf(
                        "*" to { MultiplyOperator() },
                        "/" to { DivisionOperator() },
                    ),
            )

        val completeExpressionParRule =
            ExpressionParRule(
                listOf(
                    finalBinaryParRule,
                    unaryParRule,
                    finalGroupingParRule,
                    literalNumberParRule,
                    literalStringParRule,
                    literalIdentifierParRule,
                ),
            )

        val callableParRule = CallableParRule(completeExpressionParRule)
        val variableDeclarationParRule = VariableDeclarationParRule(setOf("let"), completeExpressionParRule)
        val variableReassignmentParRule = VariableReassignmentParRule(completeExpressionParRule)

        return DefaultParser(listOf(variableDeclarationParRule, variableReassignmentParRule, callableParRule))
    }

    private fun createParserV2(): DefaultParser {
        val literalNumberParRule = LiteralNumberParRule()
        val literalStringParRule = LiteralStringParRule()
        val literalIdentifierParRule = LiteralIdentifierParRule()
        val literalBooleanParRule = LiteralBooleanParRule()

        val baseExpressionParRule =
            ExpressionParRule(listOf(literalNumberParRule, literalStringParRule, literalIdentifierParRule, literalBooleanParRule))

        val unaryParRule = UnaryParRule(baseExpressionParRule)
        val groupingParRule = GroupingParRule(baseExpressionParRule)

        val binaryExpressionParRule =
            ExpressionParRule(listOf(unaryParRule, groupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule))

        val binaryParRule =
            BinaryParRule(
                binaryExpressionParRule,
                additionOperators =
                    mapOf(
                        "+" to { AdditionOperator() },
                        "-" to { MinusOperator() },
                    ),
                multiplicationOperators =
                    mapOf(
                        "*" to { MultiplyOperator() },
                        "/" to { DivisionOperator() },
                    ),
            )

        val booleanExpression =
            ExpressionParRule(
                listOf(
                    binaryParRule,
                    unaryParRule,
                    groupingParRule,
                    literalNumberParRule,
                    literalStringParRule,
                    literalIdentifierParRule,
                    literalBooleanParRule,
                ),
            )

        val booleanExpressionParRule =
            BooleanExpressionParRule(
                booleanExpression,
                comparisonOperators =
                    mapOf(
                        "==" to { Equals() },
                        "!=" to { NotEquals() },
                        "<" to { Lesser() },
                        ">" to { Greater() },
                        "<=" to { LesserEquals() },
                        ">=" to { GreaterEquals() },
                    ),
            )

        val fullExpressionParRule =
            ExpressionParRule(
                listOf(
                    booleanExpressionParRule,
                    binaryParRule,
                    unaryParRule,
                    groupingParRule,
                    literalNumberParRule,
                    literalStringParRule,
                    literalIdentifierParRule,
                ),
            )

        val finalGroupingParRule = GroupingParRule(fullExpressionParRule)

        val finalBinaryExpressionParRule =
            ExpressionParRule(
                listOf(unaryParRule, finalGroupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule),
            )

        val finalBinaryParRule =
            BinaryParRule(
                finalBinaryExpressionParRule,
                additionOperators =
                    mapOf(
                        "+" to { AdditionOperator() },
                        "-" to { MinusOperator() },
                    ),
                multiplicationOperators =
                    mapOf(
                        "*" to { MultiplyOperator() },
                        "/" to { DivisionOperator() },
                    ),
            )

        val finalBooleanExpression =
            ExpressionParRule(
                listOf(
                    finalBinaryParRule,
                    unaryParRule,
                    finalGroupingParRule,
                    literalNumberParRule,
                    literalStringParRule,
                    literalIdentifierParRule,
                    literalBooleanParRule,
                ),
            )

        val finalBooleanRule =
            BooleanExpressionParRule(
                finalBooleanExpression,
                comparisonOperators =
                    mapOf(
                        "==" to { Equals() },
                        "!=" to { NotEquals() },
                        "<" to { Lesser() },
                        ">" to { Greater() },
                        "<=" to { LesserEquals() },
                        ">=" to { GreaterEquals() },
                    ),
            )

        val completeExpressionParRule =
            ExpressionParRule(
                listOf(
                    finalBooleanRule,
                    finalBinaryParRule,
                    unaryParRule,
                    finalGroupingParRule,
                    literalNumberParRule,
                    literalStringParRule,
                    literalIdentifierParRule,
                ),
            )
        val callableCall = CallableCallParRule(completeExpressionParRule)
        val expression = ExpressionParRule(listOf(completeExpressionParRule, callableCall))

        val completeExpressionForCallable =
            ExpressionParRule(
                listOf(
                    finalBooleanRule,
                    finalBinaryParRule,
                    unaryParRule,
                    finalGroupingParRule,
                    callableCall,
                    literalNumberParRule,
                    literalStringParRule,
                    literalIdentifierParRule,
                ),
            )
        val callableParRule = CallableParRule(completeExpressionForCallable)
        val variableDeclarationParRule = VariableDeclarationParRule(setOf("let"), expression)
        val variableReassignmentParRule = VariableReassignmentParRule(expression)
        val constantDeclarationRule = ConstDeclarationParRule(setOf("const"), expression)
        val statementRules =
            listOf(
                variableDeclarationParRule,
                variableReassignmentParRule,
                constantDeclarationRule,
                callableParRule,
                callableCall,
            )

        val conditionalParRule = ConditionalParRule(finalBooleanRule, statementRules)
        val finalStatements =
            listOf(
                conditionalParRule,
                variableDeclarationParRule,
                variableReassignmentParRule,
                constantDeclarationRule,
                callableParRule,
                callableCall,
            )
        val finalConditionalParRule = ConditionalParRule(finalBooleanRule, finalStatements)

        return DefaultParser(
            listOf(
                finalConditionalParRule,
                variableDeclarationParRule,
                variableReassignmentParRule,
                constantDeclarationRule,
                callableParRule,
            ),
        )
    }
}
