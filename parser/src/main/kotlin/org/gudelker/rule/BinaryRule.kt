package org.gudelker.rule

import org.example.org.gudelker.Binary
import org.example.org.gudelker.ExpressionStatement
import org.example.org.gudelker.operator.DivisionOperator
import org.example.org.gudelker.operator.MinusOperator
import org.example.org.gudelker.operator.MultiplyOperator
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.Operator
import org.gudelker.result.ParseResult
import org.gudelker.result.SyntaxError
import org.gudelker.result.ValidStatementResult
import org.gudelker.tokenstream.TokenStream

class BinaryRule(
    private val expressionRule: SyntaxRule,
    private val operatorTypes: Set<TokenType> = setOf(TokenType.OPERATOR),
) : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return expressionRule.matches(tokenStream)
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        return parseAddition(tokenStream)
    }

    // Maneja operadores de suma/resta (menor precedencia)
    private fun parseAddition(stream: TokenStream): ParseResult {
        val leftResult = parseMultiplication(stream)
        if (leftResult.result !is ValidStatementResult) {
            return leftResult
        }

        return parseAdditionContinuation(
            leftResult.result.getStatement() as ExpressionStatement,
            leftResult.tokenStream,
        )
    }

    private fun parseAdditionContinuation(
        leftExpression: ExpressionStatement,
        stream: TokenStream,
    ): ParseResult {
        val currentToken = stream.current()

        return if (currentToken?.getType() == TokenType.OPERATOR &&
            currentToken.getValue() in setOf("+", "-")
        ) {
            val operator =
                createOperator(currentToken.getValue())
                    ?: return ParseResult(
                        SyntaxError("Operador no válido: ${currentToken.getValue()}"),
                        stream,
                    )

            val (_, streamAfterOperator) = stream.next()
            val rightResult = parseMultiplication(streamAfterOperator)

            if (rightResult.result !is ValidStatementResult) {
                return rightResult
            }

            val binaryExpression =
                Binary(
                    leftExpression = leftExpression,
                    operator = operator,
                    rightExpression = rightResult.result.getStatement() as ExpressionStatement,
                )

            // Recursivamente continuar parseando más operadores del mismo nivel
            parseAdditionContinuation(binaryExpression, rightResult.tokenStream)
        } else {
            // No hay más operadores de suma/resta, devolver la expresión actual
            ParseResult(ValidStatementResult(leftExpression), stream)
        }
    }

    // Maneja operadores de multiplicación/división (mayor precedencia)
    private fun parseMultiplication(stream: TokenStream): ParseResult {
        val leftResult = expressionRule.parse(stream)
        if (leftResult.result !is ValidStatementResult) {
            return leftResult
        }

        return parseMultiplicationContinuation(
            leftResult.result.getStatement() as ExpressionStatement,
            leftResult.tokenStream,
        )
    }

    private fun parseMultiplicationContinuation(
        leftExpression: ExpressionStatement,
        stream: TokenStream,
    ): ParseResult {
        println(leftExpression)
        val currentToken = stream.current()

        return if (currentToken?.getType() == TokenType.OPERATOR &&
            currentToken.getValue() in setOf("*", "/")
        ) {
            val operator =
                createOperator(currentToken.getValue())
                    ?: return ParseResult(
                        SyntaxError("Operador no válido: ${currentToken.getValue()}"),
                        stream,
                    )

            val (_, streamAfterOperator) = stream.next()
            val rightResult = expressionRule.parse(streamAfterOperator)

            if (rightResult.result !is ValidStatementResult) {
                return rightResult
            }

            val binaryExpression =
                Binary(
                    leftExpression = leftExpression,
                    operator = operator,
                    rightExpression = rightResult.result.getStatement() as ExpressionStatement,
                )

            // Recursivamente continuar parseando más operadores del mismo nivel
            parseMultiplicationContinuation(binaryExpression, rightResult.tokenStream)
        } else {
            // No hay más operadores de multiplicación/división, devolver la expresión actual
            ParseResult(ValidStatementResult(leftExpression), stream)
        }
    }

    private fun createOperator(operatorValue: String): Operator? {
        return when (operatorValue) {
            "+" -> AdditionOperator()
            "-" -> MinusOperator()
            "*" -> MultiplyOperator()
            "/" -> DivisionOperator()
            else -> null
        }
    }
}
