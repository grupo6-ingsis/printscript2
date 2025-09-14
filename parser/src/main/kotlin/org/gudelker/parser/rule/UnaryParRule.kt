package org.gudelker.parser.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.Binary
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.expressions.LiteralBoolean
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.expressions.Unary
import org.gudelker.operators.Operator
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

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
        // Get operator position
        val operatorPos = token.getPosition()
        val operatorPosition =
            StatementPosition(
                operatorPos.startLine,
                operatorPos.startColumn,
                operatorPos.endLine,
                operatorPos.endColumn,
            )
        val (_, streamAfterOperator) = tokenStream.next()
        return parseUnaryExpression(operator, streamAfterOperator, operatorPosition)
    }

    private fun isUnaryOperator(token: org.gudelker.Token?): Boolean {
        return token?.getType() == TokenType.OPERATOR && unaryOperators.containsKey(token.getValue())
    }

    private fun parseUnaryExpression(
        operator: Operator,
        tokenStream: TokenStream,
        operatorPosition: StatementPosition,
    ): ParseResult {
        val expressionResult = expressionRule.parse(tokenStream)
        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return expressionResult
        }
        val expression = expressionResult.parserResult.getStatement()
        if (expression !is ExpressionStatement) {
            return errorResult("Se esperaba una expresión después del operador unario", expressionResult.tokenStream)
        }
        // Get expression position and calculate overall position
        val exprPos = getExpressionEndPosition(expression)
        val overallPosition =
            if (exprPos != null) {
                StatementPosition(
                    operatorPosition.startLine,
                    operatorPosition.startColumn,
                    exprPos.endLine,
                    exprPos.endColumn,
                )
            } else {
                null
            }
        val operatorCombo = ComboValuePosition(operator, operatorPosition)

        val unaryExpression =
            Unary(
                value = expression,
                operator = operatorCombo,
                position = overallPosition,
            )
        return ParseResult(ValidStatementParserResult(unaryExpression), expressionResult.tokenStream)
    }

    private fun errorResult(
        message: String,
        stream: TokenStream,
    ) = ParseResult(ParserSyntaxError(message), stream)

    private fun getExpressionEndPosition(expr: ExpressionStatement): StatementPosition? {
        return when (expr) {
            is LiteralString -> expr.value.position
            is LiteralNumber -> expr.value.position
            is LiteralBoolean -> expr.value.position
            is LiteralIdentifier -> expr.value.position
            is Binary -> expr.position ?: getExpressionEndPosition(expr.rightExpression)
            is Unary -> expr.position ?: getExpressionEndPosition(expr.value)
            else -> null
        }
    }
}
