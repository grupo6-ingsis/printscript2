package org.gudelker.formatter

import org.gudelker.analyzer.BinaryForAnalyzer
import org.gudelker.analyzer.BooleanExprForAnalyzer
import org.gudelker.analyzer.CallableCallForAnalyzer
import org.gudelker.analyzer.CallableForAnalyzer
import org.gudelker.analyzer.ConditionalExprForAnalyzer
import org.gudelker.analyzer.ConstDeclarationForAnalyzer
import org.gudelker.analyzer.GroupingForAnalyzer
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
        val ruleValidors =
            listOf(
                SpaceBeforeColon(),
                SpaceAfterColon(),
                SpacesAroundAssignation(),
                LineBreakAfterStatement(),
                NormalizeDeclarationIndentation(),
                SingleSpaceSeparationRule(),
            )
        val analyzers =
            listOf(
                VariableDeclarationForAnalyzer(
                    ruleValidors,
                ),
                LiteralNumberForAnalyzer(),
                LiteralIdentifierForAnalyzer(),
                LiteralStringForAnalyzer(),
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
        return DefaultFormatter(analyzers)
    }

    private fun createFormatterV2(): DefaultFormatter {
        val ruleValidators =
            listOf(
                SpaceBeforeColon(),
                SpaceAfterColon(),
                SpacesAroundAssignation(),
                LineBreakAfterStatement(),
                NormalizeDeclarationIndentation(),
                IfIndentation(),
                SingleSpaceSeparationRule(),
            )
        val analyzers =
            listOf(
                VariableDeclarationForAnalyzer(
                    ruleValidators,
                ),
                LiteralNumberForAnalyzer(),
                LiteralIdentifierForAnalyzer(),
                LiteralStringForAnalyzer(),
                GroupingForAnalyzer(),
                UnaryForAnalyzer(),
                CallableForAnalyzer(
                    listOf(SpacesPrintln(), LineBreakAfterStatement(), SingleSpaceSeparationRule()),
                ),
                CallableCallForAnalyzer(emptyList()),
                BinaryForAnalyzer(),
                VariableReassignmentForAnalyzer(
                    listOf(SpacesAroundAssignation(), SingleSpaceSeparationRule()),
                ),
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
        return DefaultFormatter(analyzers)
    }
}
