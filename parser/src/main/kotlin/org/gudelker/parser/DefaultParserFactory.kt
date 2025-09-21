package org.gudelker.parser

import org.gudelker.parser.rule.BinaryParRule
import org.gudelker.parser.rule.BooleanExpressionParRule
import org.gudelker.parser.rule.CallableCallParRule
import org.gudelker.parser.rule.ExpressionParRule
import org.gudelker.parser.rule.GroupingParRule
import org.gudelker.parser.rule.LiteralBooleanParRule
import org.gudelker.parser.rule.LiteralIdentifierParRule
import org.gudelker.parser.rule.LiteralNumberParRule
import org.gudelker.parser.rule.LiteralStringParRule
import org.gudelker.parser.rule.SyntaxParRule
import org.gudelker.parser.rule.UnaryParRule
import org.gudelker.utilities.Version

object DefaultParserFactory {
    fun createParser(version: Version): DefaultParser {
        return when (version) {
            Version.V1 -> createParserV1()
            Version.V2 -> createParserV2()
        }
    }

    private fun createParserV1(): DefaultParser {
        return buildParser(
            includeBoolean = false,
            buildExtraRules = { completeExpressionRule ->
                ParserFactoryUtils.createStatementRulesV1(completeExpressionRule)
            },
        )
    }

    private fun createParserV2(): DefaultParser {
        return buildParser(
            includeBoolean = true,
            buildExtraRules = { completeExpressionRule ->
                val callableCall = CallableCallParRule(completeExpressionRule)
                val expression = ParserFactoryUtils.createExpressionRule(listOf(completeExpressionRule, callableCall))

                val completeExpressionForCallable =
                    ParserFactoryUtils.createExpressionWithCallableCall(
                        finalBooleanRule = completeExpressionRule.getRules().filterIsInstance<BooleanExpressionParRule>().first(),
                        finalBinaryRule = completeExpressionRule.getRules().filterIsInstance<BinaryParRule>().first(),
                        unaryRule = completeExpressionRule.getRules().filterIsInstance<UnaryParRule>().first(),
                        finalGroupingRule = completeExpressionRule.getRules().filterIsInstance<GroupingParRule>().first(),
                        literalRules =
                            completeExpressionRule.getRules().filter {
                                it is LiteralNumberParRule || it is LiteralStringParRule ||
                                    it is LiteralIdentifierParRule || it is LiteralBooleanParRule
                            },
                        callableCall = callableCall,
                    )

                ParserFactoryUtils.createStatementRulesV2(
                    expression,
                    completeExpressionForCallable,
                    completeExpressionRule.getRules().filterIsInstance<BooleanExpressionParRule>().first(),
                    callableCall,
                )
            },
        )
    }

    private fun buildParser(
        includeBoolean: Boolean,
        buildExtraRules: (ExpressionParRule) -> List<SyntaxParRule>,
    ): DefaultParser {
        val literalRules = ParserFactoryUtils.createLiteralRules(includeBoolean)
        val baseExpressionRule = ParserFactoryUtils.createExpressionRule(literalRules)
        val unaryRule = ParserFactoryUtils.createUnaryRule(baseExpressionRule)
        val groupingRule = ParserFactoryUtils.createGroupingRule(baseExpressionRule)

        val literalsForBinary =
            if (includeBoolean) {
                literalRules.filterNot { it is LiteralBooleanParRule }
            } else {
                literalRules
            }
        val binaryExpressionRule = ParserFactoryUtils.createExpressionRule(listOf(unaryRule, groupingRule) + literalsForBinary)
        val binaryRule = ParserFactoryUtils.createBinaryRule(binaryExpressionRule)

        val completeExpressionComponents =
            if (includeBoolean) {
                val booleanExpression = ParserFactoryUtils.createExpressionRule(listOf(binaryRule, unaryRule, groupingRule) + literalRules)
                val booleanRule = ParserFactoryUtils.createBooleanExpressionRule(booleanExpression)
                ParserFactoryUtils.buildFinalExpressionComponents(
                    booleanRule = booleanRule,
                    binaryRule = binaryRule,
                    unaryRule = unaryRule,
                    groupingRule = groupingRule,
                    literalRules = literalRules,
                )
            } else {
                ParserFactoryUtils.buildFinalExpressionComponents(
                    booleanRule = null,
                    binaryRule = binaryRule,
                    unaryRule = unaryRule,
                    groupingRule = groupingRule,
                    literalRules = literalRules,
                )
            }

        val completeExpressionRule = ParserFactoryUtils.createExpressionRule(completeExpressionComponents)
        val statementRules = buildExtraRules(completeExpressionRule)

        return DefaultParser(statementRules)
    }
}
