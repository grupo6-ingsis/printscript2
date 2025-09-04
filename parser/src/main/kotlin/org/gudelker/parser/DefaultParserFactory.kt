package org.gudelker.parser

import org.gudelker.rule.BinaryParRule
import org.gudelker.rule.BooleanExpressionParRule
import org.gudelker.rule.CallableParRule
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

        // Crear reglas base sin dependencias circulares
        val baseExpressionParRule = ExpressionParRule(listOf(literalNumberParRule, literalStringParRule, literalIdentifierParRule))

        // UnaryRule que puede acceder a literales
        val unaryParRule = UnaryParRule(baseExpressionParRule)

        // Crear un placeholder para la expresión completa
        val fullExpressionParRule: ExpressionParRule

        // GroupingRule que usará la expresión completa
        val groupingParRule = GroupingParRule(baseExpressionParRule) // temporal

        // BinaryRule con acceso a unary, grouping y literales
        val binaryExpressionParRule =
            ExpressionParRule(listOf(unaryParRule, groupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule))
        val binaryParRule = BinaryParRule(binaryExpressionParRule)

        // Expresión completa con todas las reglas
        fullExpressionParRule =
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

        // GroupingRule con la expresión completa
        val finalGroupingParRule = GroupingParRule(fullExpressionParRule)

        // BinaryRule con acceso completo
        val finalBinaryExpressionParRule =
            ExpressionParRule(
                listOf(unaryParRule, finalGroupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule),
            )
        val finalBinaryParRule = BinaryParRule(finalBinaryExpressionParRule)

        // Expresión final con todas las reglas actualizadas
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

        // Crear reglas base sin dependencias circulares
        val baseExpressionParRule =
            ExpressionParRule(listOf(literalNumberParRule, literalStringParRule, literalIdentifierParRule, literalBooleanParRule))

        // UnaryRule que puede acceder a literales
        val unaryParRule = UnaryParRule(baseExpressionParRule)

        // Crear un placeholder para la expresión completa
        val fullExpressionParRule: ExpressionParRule

        // GroupingRule que usará la expresión completa
        val groupingParRule = GroupingParRule(baseExpressionParRule) // temporal

        // BinaryRule con acceso a unary, grouping y literales
        val binaryExpressionParRule =
            ExpressionParRule(listOf(unaryParRule, groupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule))
        val binaryParRule = BinaryParRule(binaryExpressionParRule)
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
        val booleanExpressionParRule = BooleanExpressionParRule(booleanExpression)

        // Expresión completa con todas las reglas
        fullExpressionParRule =
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

        // GroupingRule con la expresión completa
        val finalGroupingParRule = GroupingParRule(fullExpressionParRule)

        // BinaryRule con acceso completo
        val finalBinaryExpressionParRule =
            ExpressionParRule(
                listOf(unaryParRule, finalGroupingParRule, literalNumberParRule, literalStringParRule, literalIdentifierParRule),
            )
        val finalBinaryParRule = BinaryParRule(finalBinaryExpressionParRule)
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
        val finalBooleanRule = BooleanExpressionParRule(finalBooleanExpression)

        // Expresión final con todas las reglas actualizadas
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

        val callableParRule = CallableParRule(completeExpressionParRule)
        val variableDeclarationParRule = VariableDeclarationParRule(setOf("let"), completeExpressionParRule)
        val variableReassignmentParRule = VariableReassignmentParRule(completeExpressionParRule)
        val constantDeclarationRule = ConstDeclarationParRule(setOf("const"), completeExpressionParRule)

        return DefaultParser(listOf(variableDeclarationParRule, variableReassignmentParRule, constantDeclarationRule, callableParRule))
    }
}
