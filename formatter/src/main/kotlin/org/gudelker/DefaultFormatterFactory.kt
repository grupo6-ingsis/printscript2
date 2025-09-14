package org.gudelker

import org.gudelker.utilities.Version

object DefaultFormatterFactory {
    fun createFormatter(version: Version): DefaultFormatter {
        return when (version) {
            Version.V1 -> createFormatterV1()
            Version.V2 -> createFormatterV2()
        }
    }

    private fun createFormatterV1(): DefaultFormatter {
        TODO()
//        val ruleValidors =
//            listOf(
//                SpaceBeforeColon(),
//                SpaceAfterColon(),
//                SpacesAroundAssignation(),
//            )
//        val analyzers =
//            listOf(
//                VariableDeclarationForAnalyzer(
//                    ruleValidors,
//                ),
//                LiteralNumberForAnalyzer(),
//                LiteralIdentifierForAnalyzer(),
//                LiteralStringForAnalyzer(),
//                GroupingForAnalyzer(),
//                UnaryForAnalyzer(),
//                CallableForAnalyzer(
//                    listOf(SpacesPrintln()),
//                ),
//                BinaryForAnalyzer(),
//                VariableReassignmentForAnalyzer(
//                    listOf(SpacesAroundAssignation()),
//                ),
//            )
//        return DefaultFormatter(analyzers)
    }

    private fun createFormatterV2(): DefaultFormatter {
        TODO()
//        val ruleValidators =
//            listOf(
//                SpaceBeforeColon(),
//                SpaceAfterColon(),
//                SpacesAroundAssignation(),
//                IfIndentation(),
//            )
//        val analyzers =
//            listOf(
//                VariableDeclarationForAnalyzer(
//                    ruleValidators,
//                ),
//                LiteralNumberForAnalyzer(),
//                LiteralIdentifierForAnalyzer(),
//                LiteralStringForAnalyzer(),
//                GroupingForAnalyzer(),
//                UnaryForAnalyzer(),
//                CallableForAnalyzer(
//                    listOf(SpacesPrintln()),
//                ),
//                CallableCallForAnalyzer(emptyList()),
//                BinaryForAnalyzer(),
//                VariableReassignmentForAnalyzer(
//                    listOf(SpacesAroundAssignation()),
//                ),
//                BooleanExprForAnalyzer(),
//                ConditionalExprForAnalyzer(
//                    listOf(
//                        IfIndentation(),
//                    ),
//                ),
//                ConstDeclarationForAnalyzer(
//                    ruleValidators,
//                ),
//                LiteralBooleanForAnalyzer(),
//            )
//        return DefaultFormatter(analyzers)
    }
}
