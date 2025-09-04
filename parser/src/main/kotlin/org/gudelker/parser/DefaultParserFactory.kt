package org.gudelker.parser

import org.gudelker.rule.BinaryRule
import org.gudelker.rule.BooleanEpressionRule
import org.gudelker.rule.CallableRule
import org.gudelker.rule.ConstDeclarationRule
import org.gudelker.rule.ExpressionRule
import org.gudelker.rule.GroupingRule
import org.gudelker.rule.LiteralBooleanRule
import org.gudelker.rule.LiteralIdentifierRule
import org.gudelker.rule.LiteralNumberRule
import org.gudelker.rule.LiteralStringRule
import org.gudelker.rule.UnaryRule
import org.gudelker.rule.VariableDeclarationRule
import org.gudelker.rule.VariableReassignmentRule
import org.gudelker.utilities.Version

object DefaultParserFactory {
    fun createParser(version: Version): DefaultParser {
        return when (version) {
            Version.V1 -> createParserV1()
            Version.V2 -> createParserV2()
        }
    }

    private fun createParserV1(): DefaultParser {
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

        return DefaultParser(listOf(variableDeclarationRule, variableReassignmentRule, callableRule))
    }

    private fun createParserV2(): DefaultParser  {
        val literalNumberRule = LiteralNumberRule()
        val literalStringRule = LiteralStringRule()
        val literalIdentifierRule = LiteralIdentifierRule()
        val literalBooleanRule = LiteralBooleanRule()

        // Crear reglas base sin dependencias circulares
        val baseExpressionRule = ExpressionRule(listOf(literalNumberRule, literalStringRule, literalIdentifierRule, literalBooleanRule))

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
        val booleanExpression =
            ExpressionRule(
                listOf(
                    binaryRule,
                    unaryRule,
                    groupingRule,
                    literalNumberRule,
                    literalStringRule,
                    literalIdentifierRule,
                    literalBooleanRule,
                ),
            )
        val booleanExpressionRule = BooleanEpressionRule(booleanExpression)

        // Expresión completa con todas las reglas
        fullExpressionRule =
            ExpressionRule(
                listOf(
                    booleanExpressionRule,
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
        val finalBooleanExpression =
            ExpressionRule(
                listOf(
                    finalBinaryRule,
                    unaryRule,
                    finalGroupingRule,
                    literalNumberRule,
                    literalStringRule,
                    literalIdentifierRule,
                    literalBooleanRule,
                ),
            )
        val finalBooleanRule = BooleanEpressionRule(finalBooleanExpression)

        // Expresión final con todas las reglas actualizadas
        val completeExpressionRule =
            ExpressionRule(
                listOf(
                    finalBooleanRule,
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
        val constantDeclarationRule = ConstDeclarationRule(setOf("const"), completeExpressionRule)

        return DefaultParser(listOf(variableDeclarationRule, variableReassignmentRule, constantDeclarationRule, callableRule))
    }
}
