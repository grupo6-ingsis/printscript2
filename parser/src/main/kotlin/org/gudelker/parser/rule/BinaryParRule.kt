package org.gudelker.parser.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.Binary
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.operators.Operator
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream

class BinaryParRule(
    private val expressionRule: SyntaxParRule,
    private val additionOperators: Map<String, () -> Operator> = emptyMap(),
    private val multiplicationOperators: Map<String, () -> Operator> = emptyMap(),
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return expressionRule.matches(tokenStream)
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        return parseAddition(tokenStream)
    }

    private fun parseAddition(stream: TokenStream): ParseResult {
        val leftResult = parseMultiplication(stream)
        if (leftResult.parserResult !is ValidStatementParserResult) {
            return leftResult
        }

        return parseAdditionContinuation(
            leftResult.parserResult.getStatement() as ExpressionStatement,
            leftResult.tokenStream,
        )
    }

    private fun parseAdditionContinuation(
        leftExpression: ExpressionStatement,
        stream: TokenStream,
    ): ParseResult {
        val currentToken = stream.current()

        return if (currentToken?.getType() == TokenType.OPERATOR &&
            currentToken.getValue() in additionOperators.keys
        ) {
            val operator =
                additionOperators[currentToken.getValue()]?.invoke()
                    ?: return ParseResult(
                        ParserSyntaxError("Operador no válido: ${currentToken.getValue()}"),
                        stream,
                    )

            val (_, streamAfterOperator) = stream.next()
            val rightResult = parseMultiplication(streamAfterOperator)

            if (rightResult.parserResult !is ValidStatementParserResult) {
                return rightResult
            }

            val binaryExpression =
                Binary(
                    leftExpression = leftExpression,
                    operator = operator,
                    rightExpression = rightResult.parserResult.getStatement() as ExpressionStatement,
                )

            parseAdditionContinuation(binaryExpression, rightResult.tokenStream)
        } else {
            ParseResult(ValidStatementParserResult(leftExpression), stream)
        }
    }

    private fun parseMultiplication(stream: TokenStream): ParseResult {
        val leftResult = expressionRule.parse(stream)
        if (leftResult.parserResult !is ValidStatementParserResult) {
            return leftResult
        }

        return parseMultiplicationContinuation(
            leftResult.parserResult.getStatement() as ExpressionStatement,
            leftResult.tokenStream,
        )
    }

    private fun parseMultiplicationContinuation(
        leftExpression: ExpressionStatement,
        stream: TokenStream,
    ): ParseResult {
        val currentToken = stream.current()

        return if (currentToken?.getType() == TokenType.OPERATOR &&
            currentToken.getValue() in multiplicationOperators.keys
        ) {
            val operator =
                multiplicationOperators[currentToken.getValue()]?.invoke()
                    ?: return ParseResult(
                        ParserSyntaxError("Operador no válido: ${currentToken.getValue()}"),
                        stream,
                    )

            val (_, streamAfterOperator) = stream.next()
            val rightResult = expressionRule.parse(streamAfterOperator)

            if (rightResult.parserResult !is ValidStatementParserResult) {
                return rightResult
            }

            val binaryExpression =
                Binary(
                    leftExpression = leftExpression,
                    operator = operator,
                    rightExpression = rightResult.parserResult.getStatement() as ExpressionStatement,
                )

            parseMultiplicationContinuation(binaryExpression, rightResult.tokenStream)
        } else {
            ParseResult(ValidStatementParserResult(leftExpression), stream)
        }
    }
}
