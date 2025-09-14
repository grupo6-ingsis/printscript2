package org.gudelker.parser.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.expressions.Unary
import org.gudelker.operators.Operator
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream

class UnaryParRule(
    private val expressionRule: SyntaxParRule,
    private val unaryOperators: Map<String, () -> Operator>,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        val token = tokenStream.current()
        return isUnaryOperator(token)
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        if (!isUnaryOperator(token)) {
            return errorResult("Se esperaba un operador unario válido", tokenStream)
        }
        val operator =
            unaryOperators[token!!.getValue()]?.invoke()
                ?: return errorResult("Operador unario no válido: ${token.getValue()}", tokenStream)
        val (_, streamAfterOperator) = tokenStream.next()
        return parseUnaryExpression(operator, streamAfterOperator)
    }

    private fun isUnaryOperator(token: org.gudelker.Token?): Boolean {
        return token?.getType() == TokenType.OPERATOR && unaryOperators.containsKey(token.getValue())
    }

    private fun parseUnaryExpression(
        operator: Operator,
        tokenStream: TokenStream,
    ): ParseResult {
        val expressionResult = expressionRule.parse(tokenStream)
        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return expressionResult
        }
        val expression = expressionResult.parserResult.getStatement()
        if (expression !is ExpressionStatement) {
            return errorResult("Se esperaba una expresión después del operador unario", expressionResult.tokenStream)
        }
        val unaryExpression = Unary(expression, operator)
        return ParseResult(ValidStatementParserResult(unaryExpression), expressionResult.tokenStream)
    }

    private fun errorResult(
        message: String,
        stream: TokenStream,
    ) = ParseResult(ParserSyntaxError(message), stream)
}
