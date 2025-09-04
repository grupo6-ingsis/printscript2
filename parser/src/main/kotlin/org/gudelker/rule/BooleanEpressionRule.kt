package org.gudelker.rule

import org.gudelker.BooleanExpression
import org.gudelker.ExpressionStatement
import org.gudelker.comparator.Comparator
import org.gudelker.comparator.Equals
import org.gudelker.comparator.Greater
import org.gudelker.comparator.GreaterEquals
import org.gudelker.comparator.Lesser
import org.gudelker.comparator.LesserEquals
import org.gudelker.comparator.NotEquals
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.tokenstream.TokenStream

class BooleanEpressionRule(
    private val expressionRule: SyntaxRule,
    private val comparisonOperators: Set<String> = setOf("==", "!=", "<", ">", "<=", ">="),
) : SyntaxRule {
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
            currentToken.getValue() in comparisonOperators
        ) {
            val comparator =
                createComparator(currentToken.getValue())
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

            // Recursivamente continuar parseando más comparadores
            parseComparisonContinuation(booleanExpression, rightResult.tokenStream)
        } else {
            // No hay más comparadores, devolver la expresión actual
            ParseResult(ValidStatementParserResult(leftExpression), stream)
        }
    }

    private fun createComparator(operatorValue: String): Comparator? {
        return when (operatorValue) {
            "==" -> Equals()
            "!=" -> NotEquals()
            "<" -> Lesser()
            ">" -> Greater()
            "<=" -> LesserEquals()
            ">=" -> GreaterEquals()
            else -> null
        }
    }
}
