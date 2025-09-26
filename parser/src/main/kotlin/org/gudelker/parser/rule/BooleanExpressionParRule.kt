package org.gudelker.parser.rule

import org.gudelker.compare.operators.Comparator
import org.gudelker.expressions.BooleanExpression
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.token.TokenType

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
        return if (isComparatorToken(currentToken)) {
            val comparator =
                getComparator(currentToken!!.getValue())
                    ?: return ParseResult(
                        ParserSyntaxError("Comparador no válido: ${currentToken.getValue()}"),
                        stream,
                    )
            val (_, streamAfterComparator) = stream.next()
            val rightResult = expressionRule.parse(streamAfterComparator)
            if (rightResult.parserResult !is ValidStatementParserResult) {
                return rightResult
            }
            val rightExpression = rightResult.parserResult.getStatement() as ExpressionStatement
            val booleanExpression = createBooleanExpression(leftExpression, comparator, rightExpression)
            parseComparisonContinuation(booleanExpression, rightResult.tokenStream)
        } else {
            ParseResult(ValidStatementParserResult(leftExpression), stream)
        }
    }

    private fun isComparatorToken(token: org.gudelker.token.Token?): Boolean {
        return token?.getType() == TokenType.COMPARATOR &&
            token.getValue() in comparisonOperators.keys
    }

    private fun getComparator(value: String): Comparator? {
        return comparisonOperators[value]?.invoke()
    }

    private fun createBooleanExpression(
        left: ExpressionStatement,
        comparator: Comparator,
        right: ExpressionStatement,
    ): BooleanExpression {
        return BooleanExpression(left = left, comparator = comparator, right = right)
    }
}
