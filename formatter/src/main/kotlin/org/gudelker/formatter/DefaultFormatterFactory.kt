package org.gudelker.formatter

import org.gudelker.analyzer.Analyzer
import org.gudelker.analyzer.BinaryForAnalyzer
import org.gudelker.analyzer.BooleanExprForAnalyzer
import org.gudelker.analyzer.CallableForAnalyzer
import org.gudelker.analyzer.ConditionalExprForAnalyzer
import org.gudelker.analyzer.ConstDeclarationForAnalyzer
import org.gudelker.analyzer.GroupingForAnalyzer
import org.gudelker.analyzer.InvocableForAnalyzer
import org.gudelker.analyzer.LiteralBooleanForAnalyzer
import org.gudelker.analyzer.LiteralIdentifierForAnalyzer
import org.gudelker.analyzer.LiteralNumberForAnalyzer
import org.gudelker.analyzer.LiteralStringForAnalyzer
import org.gudelker.analyzer.UnaryForAnalyzer
import org.gudelker.analyzer.VariableDeclarationForAnalyzer
import org.gudelker.analyzer.VariableReassignmentForAnalyzer
import org.gudelker.rulevalidator.IfBraceBelowLine
import org.gudelker.rulevalidator.IfBraceSameLine
import org.gudelker.rulevalidator.IfIndentation
import org.gudelker.rulevalidator.LineBreakAfterStatement
import org.gudelker.rulevalidator.NormalizeDeclarationIndentation
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.rulevalidator.SingleSpaceSeparationRule
import org.gudelker.rulevalidator.SpaceAfterColon
import org.gudelker.rulevalidator.SpaceBeforeColon
import org.gudelker.rulevalidator.SpacesAroundAssignation
import org.gudelker.rulevalidator.SpacesPrintln
import org.gudelker.utilities.Version

object DefaultFormatterFactory {
    fun createFormatter(version: Version): DefaultFormatter {
        return when (version) {
            Version.V1 -> createFormatterV1()
            Version.V2 -> createFormatterV2()
        }
    }

    private fun createFormatterV1(): DefaultFormatter {
        val analyzers = createAnalyzersV1()
        return DefaultFormatter(analyzers)
    }

    private fun createFormatterV2(): DefaultFormatter {
        val analyzers = createAnalyzersV2()
        return DefaultFormatter(analyzers)
    }

    private fun createRuleValidatorsV1(): List<RuleValidatorFormatter> {
        return listOf(
            SpaceBeforeColon(),
            SpaceAfterColon(),
            SpacesAroundAssignation(),
            LineBreakAfterStatement(),
            NormalizeDeclarationIndentation(),
            SingleSpaceSeparationRule(),
        )
    }

    private fun createRuleValidatorsV2(): List<RuleValidatorFormatter> {
        return createRuleValidatorsV1() + IfIndentation()
    }

    private fun createAnalyzersV1(): List<Analyzer> {
        val ruleValidators = createRuleValidatorsV1()
        val analyzers =
            listOf(
                VariableDeclarationForAnalyzer(ruleValidators),
                LiteralNumberForAnalyzer(),
                LiteralIdentifierForAnalyzer(),
                LiteralStringForAnalyzer(),
                GroupingForAnalyzer(),
                UnaryForAnalyzer(),
                GroupingForAnalyzer(),
                UnaryForAnalyzer(),
                CallableForAnalyzer(
                    listOf(SpacesPrintln(), LineBreakAfterStatement(), SingleSpaceSeparationRule()),
                ),
                BinaryForAnalyzer(),
                VariableReassignmentForAnalyzer(
                    listOf(SpacesAroundAssignation(), SingleSpaceSeparationRule()),
                ),
            )
        return analyzers
    }

    private fun createAnalyzersV2(): List<Analyzer> {
        val ruleValidators = createRuleValidatorsV2()
        val analyzers =
            createAnalyzersV1() +
                listOf(
                    InvocableForAnalyzer(emptyList()),
                    BooleanExprForAnalyzer(),
                    ConditionalExprForAnalyzer(
                        listOf(
                            IfBraceSameLine(),
                            IfBraceBelowLine(),
                            IfIndentation(),
                            SingleSpaceSeparationRule(),
                        ),
                    ),
                    ConstDeclarationForAnalyzer(
                        ruleValidators,
                    ),
                    LiteralBooleanForAnalyzer(),
                )
        return analyzers
    }
}
