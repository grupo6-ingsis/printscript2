package org.gudelker.rule

import org.gudelker.BooleanExpression
import org.gudelker.ExpressionStatement
import org.gudelker.comparator.Comparator
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.tokenstream.TokenStream

class BooleanExpressionParRule(
    private val expressionRule: SyntaxParRule,
    private val comparisonOperators: Map<String, () -> Comparator> = emptyMap(),
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return expressionRule.matches(tokenStream)
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        return parseComparison(tokenStream)
    }

    private fun parseComparison(stream: TokenStream): ParseResult {
        val leftResult = expressionRule.parse(stream)
        if (leftResult.parserResult !is ValidStatementParserResult) {
            return leftResult
        }

        return parseComparisonContinuation(
            leftResult.parserResult.getStatement() as ExpressionStatement,
            leftResult.tokenStream,
        )
    }

    private fun parseComparisonContinuation(
        leftExpression: ExpressionStatement,
        stream: TokenStream,
    ): ParseResult {
        val currentToken = stream.current()

        return if (currentToken?.getType() == TokenType.COMPARATOR &&
            currentToken.getValue() in comparisonOperators.keys
        ) {
            val comparator =
                comparisonOperators[currentToken.getValue()]?.invoke()
                    ?: return ParseResult(
                        ParserSyntaxError("Comparador no válido: ${currentToken.getValue()}"),
                        stream,
                    )

            val (_, streamAfterComparator) = stream.next()
            val rightResult = expressionRule.parse(streamAfterComparator)

            if (rightResult.parserResult !is ValidStatementParserResult) {
                return rightResult
            }

            val booleanExpression =
                BooleanExpression(
                    left = leftExpression,
                    operator = comparator,
                    right = rightResult.parserResult.getStatement() as ExpressionStatement,
                )

            parseComparisonContinuation(booleanExpression, rightResult.tokenStream)
        } else {
            ParseResult(ValidStatementParserResult(leftExpression), stream)
        }
    }
}
