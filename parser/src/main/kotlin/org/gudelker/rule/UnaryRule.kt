package org.gudelker.rule

import org.gudelker.ExpressionStatement
import org.gudelker.Unary
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.operator.AdditionOperator
import org.gudelker.operator.MinusOperator
import org.gudelker.operator.Operator
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.tokenstream.TokenStream

class UnaryRule(
    private val expressionRule: SyntaxRule,
) : SyntaxRule {
    val operators = setOf("+", "-")

    override fun matches(tokenStream: TokenStream): Boolean {
        val currentToken = tokenStream.current()
        return currentToken?.getType() == TokenType.OPERATOR &&
            currentToken.getValue() in operators
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val currentToken = tokenStream.current()

        if (currentToken?.getType() != TokenType.OPERATOR ||
            currentToken.getValue() !in operators
        ) {
            return ParseResult(
                ParserSyntaxError("Se esperaba un operador unario (+, -)"),
                tokenStream,
            )
        }

        val operator =
            createUnaryOperator(currentToken.getValue())
                ?: return ParseResult(
                    ParserSyntaxError("Operador unario no válido: ${currentToken.getValue()}"),
                    tokenStream,
                )

        val (_, streamAfterOperator) = tokenStream.next()
        val expressionResult = expressionRule.parse(streamAfterOperator)

        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return expressionResult
        }

        val expression = expressionResult.parserResult.getStatement() as ExpressionStatement
        val unaryExpression = Unary(expression, operator)

        return ParseResult(
            ValidStatementParserResult(unaryExpression),
            expressionResult.tokenStream,
        )
    }

    private fun createUnaryOperator(operatorValue: String): Operator? {
        return when (operatorValue) {
            "+" -> AdditionOperator()
            "-" -> MinusOperator()
            else -> null
        }
    }
}
