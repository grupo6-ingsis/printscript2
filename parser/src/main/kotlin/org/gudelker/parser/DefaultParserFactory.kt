package org.gudelker.parser

import org.gudelker.rule.BinaryRule
import org.gudelker.rule.CallableRule
import org.gudelker.rule.ExpressionRule
import org.gudelker.rule.GroupingRule
import org.gudelker.rule.LiteralIdentifierRule
import org.gudelker.rule.LiteralNumberRule
import org.gudelker.rule.LiteralStringRule
import org.gudelker.rule.UnaryRule
import org.gudelker.rule.VariableDeclarationRule
import org.gudelker.rule.VariableReassignmentRule
import org.gudelker.tokenstream.TokenStream

object DefaultParserFactory {
    fun createParser(tokenStream: TokenStream): DefaultParser {
        val literalNumberRule = LiteralNumberRule()
        val literalStringRule = LiteralStringRule()
        val literalIdentifierRule = LiteralIdentifierRule()
        val unaryRule = UnaryRule(ExpressionRule(listOf(literalNumberRule, literalStringRule, literalIdentifierRule)))
        val expressionRuleForBinary = ExpressionRule(listOf(unaryRule, literalNumberRule, literalStringRule, literalIdentifierRule))
        val binaryRule = BinaryRule(expressionRuleForBinary)
        val groupingRule =
            GroupingRule(ExpressionRule(listOf(binaryRule, unaryRule, literalNumberRule, literalStringRule, literalIdentifierRule)))
        val callableRule =
            CallableRule(
                ExpressionRule(listOf(groupingRule, binaryRule, unaryRule, literalNumberRule, literalStringRule, literalIdentifierRule)),
            )
        val fullExpressionRule =
            ExpressionRule(listOf(groupingRule, binaryRule, unaryRule, literalNumberRule, literalStringRule, literalIdentifierRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                fullExpressionRule,
            )
        val variableReassignmentRule = VariableReassignmentRule(fullExpressionRule)

        return DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule, variableReassignmentRule, callableRule))
    }
}
