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
import org.gudelker.utilities.Version

object DefaultParserFactory {
    fun createParser(
        tokenStream: TokenStream,
        version: Version,
    ): DefaultParser {
        val literalNumberRule = LiteralNumberRule()
        val literalStringRule = LiteralStringRule()
        val literalIdentifierRule = LiteralIdentifierRule()

        // Crear reglas base sin dependencias circulares
        val baseExpressionRule = ExpressionRule(listOf(literalNumberRule, literalStringRule, literalIdentifierRule))

        // UnaryRule que puede acceder a literales
        val unaryRule = UnaryRule(baseExpressionRule)

        // Crear un placeholder para la expresión completa
        val fullExpressionRule: ExpressionRule

        // GroupingRule que usará la expresión completa
        val groupingRule = GroupingRule(baseExpressionRule) // temporal

        // BinaryRule con acceso a unary, grouping y literales
        val binaryExpressionRule =
            ExpressionRule(listOf(unaryRule, groupingRule, literalNumberRule, literalStringRule, literalIdentifierRule))
        val binaryRule = BinaryRule(binaryExpressionRule)

        // Expresión completa con todas las reglas
        fullExpressionRule =
            ExpressionRule(
                listOf(
                    binaryRule,
                    unaryRule,
                    groupingRule,
                    literalNumberRule,
                    literalStringRule,
                    literalIdentifierRule,
                ),
            )

        // GroupingRule con la expresión completa
        val finalGroupingRule = GroupingRule(fullExpressionRule)

        // BinaryRule con acceso completo
        val finalBinaryExpressionRule =
            ExpressionRule(listOf(unaryRule, finalGroupingRule, literalNumberRule, literalStringRule, literalIdentifierRule))
        val finalBinaryRule = BinaryRule(finalBinaryExpressionRule)

        // Expresión final con todas las reglas actualizadas
        val completeExpressionRule =
            ExpressionRule(
                listOf(
                    finalBinaryRule,
                    unaryRule,
                    finalGroupingRule,
                    literalNumberRule,
                    literalStringRule,
                    literalIdentifierRule,
                ),
            )

        val callableRule = CallableRule(completeExpressionRule)
        val variableDeclarationRule = VariableDeclarationRule(setOf("let"), completeExpressionRule)
        val variableReassignmentRule = VariableReassignmentRule(completeExpressionRule)

        return DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule, variableReassignmentRule, callableRule))
    }
}
