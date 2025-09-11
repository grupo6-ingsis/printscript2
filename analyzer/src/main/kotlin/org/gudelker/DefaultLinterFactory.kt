package org.gudelker

import org.gudelker.analyzers.BinaryExpressionLintAnalyzer
import org.gudelker.analyzers.BooleanExpressionLintAnalyzer
import org.gudelker.analyzers.CallableCallLintAnalyzer
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
import org.gudelker.expressions.LiteralBoolean
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.linterloader.JsonLinterConfigLoaderToMap
import org.gudelker.rulelinter.ConstDeclarationCamelCaseRule
import org.gudelker.rulelinter.ConstDeclarationSnakeCaseRule
import org.gudelker.rulelinter.RestrictPrintLnExpressions
import org.gudelker.rulelinter.RestrictReadInputExpressions
import org.gudelker.rulelinter.VariableDeclarationCamelCaseRule
import org.gudelker.rulelinter.VariableDeclarationSnakeCaseRule
import org.gudelker.utilities.Version

object DefaultLinterFactory {
    fun createLinter(
        version: Version,
        config: String,
    ): DefaultLinter {
        when (version) {
            Version.V1 -> return createLinterV1(config)
            Version.V2 -> return createLinterV2(config)
        }
    }

    private fun createLinterV1(config: String): DefaultLinter {
        val analyzers =
            listOf(
                VariableDeclarationLintAnalyzer(listOf(VariableDeclarationCamelCaseRule(), VariableDeclarationSnakeCaseRule())),
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
                config,
            )
        val linterV1 = DefaultLinter(analyzers, configLoader)
        return linterV1
    }

    private fun createLinterV2(config: String): DefaultLinter {
        val analyzers =
            listOf(
                VariableDeclarationLintAnalyzer(listOf(VariableDeclarationCamelCaseRule(), VariableDeclarationSnakeCaseRule())),
                CallableLintAnalyzer(
                    listOf(
                        RestrictPrintLnExpressions(
                            listOf(
                                LiteralString::class,
                                LiteralNumber::class,
                                LiteralIdentifier::class,
                                LiteralBoolean::class,
                            ),
                        ),
                    ),
                ),
                CallableCallLintAnalyzer(
                    listOf(
                        RestrictReadInputExpressions(
                            listOf(
                                LiteralString::class,
                                LiteralNumber::class,
                                LiteralIdentifier::class,
                                LiteralBoolean::class,
                            ),
                        ),
                    ),
                ),
                ConstDeclarationLintAnalyzer(listOf(ConstDeclarationCamelCaseRule(), ConstDeclarationSnakeCaseRule())),
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
                config,
            )
        val linterV2 = DefaultLinter(analyzers, configLoader)
        return linterV2
    }
}
