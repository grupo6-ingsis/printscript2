package org.gudelker

import org.gudelker.analyzers.BinaryExpressionLintAnalyzer
import org.gudelker.analyzers.BooleanExpressionLintAnalyzer
import org.gudelker.analyzers.CallableLintAnalyzer
import org.gudelker.analyzers.ConditionalExpressionLintAnalyzer
import org.gudelker.analyzers.ConstDeclarationLintAnalyzer
import org.gudelker.analyzers.GroupingExpressionLintAnalyzer
import org.gudelker.analyzers.LiteralBooleanLintAnalyzer
import org.gudelker.analyzers.LiteralIdentifierLintAnalyzer
import org.gudelker.analyzers.LiteralNumberLintAnalyzer
import org.gudelker.analyzers.LiteralStringLintAnalyzer
import org.gudelker.analyzers.UnaryExpressionLintAnalyzer
import org.gudelker.analyzers.VariableDeclarationLintAnalyzer
import org.gudelker.analyzers.VariableReassginationLintAnalyzer
import org.gudelker.linterloader.JsonLinterConfigLoaderToMap
import org.gudelker.rulelinter.CamelCaseRule
import org.gudelker.rulelinter.RestrictPrintLnExpressions
import org.gudelker.rulelinter.SnakeCaseRule
import org.gudelker.utilities.Version

object DefaultLinterFactory {
    fun createLinter(version: Version): Linter {
        when (version) {
            Version.V1 -> return createLinterV1()
            Version.V2 -> return createLinterV2()
        }
    }

    private fun createLinterV1(): Linter {
        val analyzers =
            listOf(
                VariableDeclarationLintAnalyzer(listOf(CamelCaseRule(), SnakeCaseRule())),
                CallableLintAnalyzer(
                    listOf(
                        RestrictPrintLnExpressions(
                            listOf(
                                LiteralString::class,
                                LiteralNumber::class,
                                LiteralIdentifier::class,
                            ),
                        ),
                    ),
                ),
                LiteralNumberLintAnalyzer(emptyList()),
                BinaryExpressionLintAnalyzer(emptyList()),
                UnaryExpressionLintAnalyzer(emptyList()),
                GroupingExpressionLintAnalyzer(emptyList()),
                VariableReassginationLintAnalyzer(emptyList()),
                LiteralStringLintAnalyzer(emptyList()),
                LiteralIdentifierLintAnalyzer(emptyList()),
            )
        val configLoader =
            JsonLinterConfigLoaderToMap(
                "/Users/pedrodelaguila/faculty/ingsis/printscript2/analyzer/src/main/kotlin/org/gudelker/linterconfig.json",
            )
        val linterV1 = DefaultLinter(analyzers, configLoader)
        return linterV1
    }

    private fun createLinterV2(): Linter {
        val analyzers =
            listOf(
                VariableDeclarationLintAnalyzer(listOf(CamelCaseRule(), SnakeCaseRule())),
                CallableLintAnalyzer(
                    listOf(
                        RestrictPrintLnExpressions(
                            listOf(
                                LiteralString::class,
                                LiteralNumber::class,
                                LiteralIdentifier::class,
                            ),
                        ),
                    ),
                ),
                ConstDeclarationLintAnalyzer(emptyList()),
                BinaryExpressionLintAnalyzer(emptyList()),
                ConditionalExpressionLintAnalyzer(emptyList()),
                BooleanExpressionLintAnalyzer(emptyList()),
                UnaryExpressionLintAnalyzer(emptyList()),
                GroupingExpressionLintAnalyzer(emptyList()),
                VariableReassginationLintAnalyzer(emptyList()),
                LiteralStringLintAnalyzer(emptyList()),
                LiteralIdentifierLintAnalyzer(emptyList()),
                LiteralBooleanLintAnalyzer(emptyList()),
                LiteralNumberLintAnalyzer(emptyList()),
            )
        val configLoader =
            JsonLinterConfigLoaderToMap(
                "/Users/pedrodelaguila/faculty/ingsis/printscript2/analyzer/src/main/kotlin/org/gudelker/linterconfig.json",
            )
        val linterV2 = DefaultLinter(analyzers, configLoader)
        return linterV2
    }
}
