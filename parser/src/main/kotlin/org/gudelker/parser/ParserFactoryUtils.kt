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
import org.gudelker.parser.rule.BinaryParRule
import org.gudelker.parser.rule.BooleanExpressionParRule
import org.gudelker.parser.rule.CallableCallParRule
import org.gudelker.parser.rule.CallableParRule
import org.gudelker.parser.rule.ConditionalParRule
import org.gudelker.parser.rule.ConstDeclarationParRule
import org.gudelker.parser.rule.ExpressionParRule
import org.gudelker.parser.rule.GroupingParRule
import org.gudelker.parser.rule.LiteralBooleanParRule
import org.gudelker.parser.rule.LiteralIdentifierParRule
import org.gudelker.parser.rule.LiteralNumberParRule
import org.gudelker.parser.rule.LiteralStringParRule
import org.gudelker.parser.rule.SyntaxParRule
import org.gudelker.parser.rule.UnaryParRule
import org.gudelker.parser.rule.VariableDeclarationParRule
import org.gudelker.parser.rule.VariableReassignmentParRule

object ParserFactoryUtils {
    internal fun buildFinalExpressionComponents(
        booleanRule: BooleanExpressionParRule?,
        binaryRule: BinaryParRule,
        unaryRule: UnaryParRule,
        groupingRule: GroupingParRule,
        literalRules: List<SyntaxParRule>,
    ): List<SyntaxParRule> {
        val finalGroupingRule =
            createGroupingRule(
                createExpressionRule(
                    listOfNotNull(booleanRule, binaryRule, unaryRule, groupingRule) + literalRules,
                ),
            )

        val literalsForBinary =
            if (booleanRule != null) {
                literalRules.filterNot { it is LiteralBooleanParRule }
            } else {
                literalRules
            }

        val finalBinaryExpressionRule =
            createExpressionRule(
                listOf(unaryRule, finalGroupingRule) + literalsForBinary,
            )

        val finalBinaryRule = createBinaryRule(finalBinaryExpressionRule)

        val components = mutableListOf(finalBinaryRule, unaryRule, finalGroupingRule)
        components.addAll(literalRules)

        if (booleanRule != null) {
            val finalBooleanExpression = createExpressionRule(components)
            val finalBooleanRule = createBooleanExpressionRule(finalBooleanExpression)
            return listOf(finalBooleanRule) + components
        }

        return components
    }

    internal fun createExpressionWithCallableCall(
        finalBooleanRule: BooleanExpressionParRule,
        finalBinaryRule: BinaryParRule,
        unaryRule: UnaryParRule,
        finalGroupingRule: GroupingParRule,
        literalRules: List<SyntaxParRule>,
        callableCall: CallableCallParRule,
    ): ExpressionParRule {
        return createExpressionRule(
            listOf(finalBooleanRule, finalBinaryRule, unaryRule, finalGroupingRule, callableCall) + literalRules,
        )
    }

    internal fun createLiteralRules(includeBoolean: Boolean): List<SyntaxParRule> {
        val rules =
            listOf(
                LiteralNumberParRule(),
                LiteralStringParRule(),
                LiteralIdentifierParRule(),
            )

        if (includeBoolean) {
            val newRules = rules + LiteralBooleanParRule()
            return newRules
        }

        return rules
    }

    internal fun createUnaryRule(expressionRule: ExpressionParRule): UnaryParRule {
        return UnaryParRule(
            expressionRule = expressionRule,
            unaryOperators =
                mapOf(
                    "+" to { AdditionOperator() },
                    "-" to { MinusOperator() },
                ),
        )
    }

    internal fun createGroupingRule(expressionRule: ExpressionParRule): GroupingParRule {
        return GroupingParRule(expressionRule)
    }

    internal fun createBinaryRule(expressionRule: ExpressionParRule): BinaryParRule {
        return BinaryParRule(
            expressionRule,
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
    }

    internal fun createBooleanExpressionRule(expressionRule: ExpressionParRule): BooleanExpressionParRule {
        return BooleanExpressionParRule(
            expressionRule,
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
    }

    internal fun createExpressionRule(rules: List<SyntaxParRule>): ExpressionParRule {
        return ExpressionParRule(rules)
    }

    internal fun createStatementRulesV1(basicRules: ExpressionParRule): List<SyntaxParRule> {
        val callableRule = CallableParRule(basicRules)
        val variableDeclarationRule = VariableDeclarationParRule(setOf("let"), basicRules)
        val variableReassignmentRule = VariableReassignmentParRule(basicRules)

        return listOf(variableDeclarationRule, variableReassignmentRule, callableRule)
    }

    internal fun createStatementRulesV2(
        basicRules: ExpressionParRule,
        callableExpressionRule: ExpressionParRule,
        booleanRule: BooleanExpressionParRule,
        callableCall: CallableCallParRule,
    ): List<SyntaxParRule> {
        val callableRule = CallableParRule(callableExpressionRule)
        val variableDeclarationRule = VariableDeclarationParRule(setOf("let"), basicRules)
        val variableReassignmentRule = VariableReassignmentParRule(basicRules)
        val constantDeclarationRule = ConstDeclarationParRule(setOf("const"), basicRules)

        val basicStatements =
            mutableListOf(
                variableDeclarationRule,
                variableReassignmentRule,
                constantDeclarationRule,
                callableRule,
                callableCall,
            )
        val conditionalRule = ConditionalParRule(booleanRule, basicStatements)
        basicStatements.add(conditionalRule)

        return listOf(
            conditionalRule,
            variableDeclarationRule,
            variableReassignmentRule,
            constantDeclarationRule,
            callableRule,
        )
    }
}
