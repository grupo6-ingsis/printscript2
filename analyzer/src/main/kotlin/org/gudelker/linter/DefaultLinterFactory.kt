package org.gudelker.linter

import org.gudelker.analyzers.BinaryExpressionLintAnalyzer
import org.gudelker.analyzers.BooleanExpressionLintAnalyzer
import org.gudelker.analyzers.CallableCallLintAnalyzer
import org.gudelker.analyzers.CallableLintAnalyzer
import org.gudelker.analyzers.ConditionalExpressionLintAnalyzer
import org.gudelker.analyzers.ConstDeclarationLintAnalyzer
import org.gudelker.analyzers.GroupingExpressionLintAnalyzer
import org.gudelker.analyzers.LinterAnalyzer
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
import org.gudelker.rulelinter.ConstDeclarationCamelCaseRule
import org.gudelker.rulelinter.ConstDeclarationSnakeCaseRule
import org.gudelker.rulelinter.RestrictPrintLnExpressions
import org.gudelker.rulelinter.RestrictReadInputExpressions
import org.gudelker.rulelinter.VariableDeclarationCamelCaseRule
import org.gudelker.rulelinter.VariableDeclarationSnakeCaseRule
import org.gudelker.utilities.Version

object DefaultLinterFactory {
    fun createLinter(version: Version): DefaultLinter {
        return when (version) {
            Version.V1 -> createLinterV1()
            Version.V2 -> createLinterV2()
        }
    }

    private fun createLinterV1(): DefaultLinter {
        val analyzers = createAnalyzersV1() + listOf(
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
            )
        )
        val linterV1 = DefaultLinter(analyzers)
        return linterV1
    }

    private fun createLinterV2(): DefaultLinter {
        val analyzers = createAnalyzersV2()
        val linterV2 = DefaultLinter(analyzers)
        return linterV2
    }

    private fun createAnalyzersV1(): List<LinterAnalyzer> {
        return listOf(
            VariableDeclarationLintAnalyzer(
                listOf(
                    VariableDeclarationCamelCaseRule(),
                    VariableDeclarationSnakeCaseRule(),
                ),
            ),
            LiteralNumberLintAnalyzer(emptyList()),
            BinaryExpressionLintAnalyzer(emptyList()),
            UnaryExpressionLintAnalyzer(emptyList()),
            GroupingExpressionLintAnalyzer(emptyList()),
            VariableReassginationLintAnalyzer(emptyList()),
            LiteralStringLintAnalyzer(emptyList()),
            LiteralIdentifierLintAnalyzer(emptyList())
        )
    }

    private fun createAnalyzersV2(): List<LinterAnalyzer> {
        return createAnalyzersV1() + listOf(
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

            ConditionalExpressionLintAnalyzer(emptyList()),
            BooleanExpressionLintAnalyzer(emptyList()),
            LiteralBooleanLintAnalyzer(emptyList())
        )
    }
}
