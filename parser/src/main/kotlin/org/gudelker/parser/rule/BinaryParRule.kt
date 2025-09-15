package org.gudelker.parser.rule
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
import org.gudelker.token.TokenType

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
            val operatorValue =
                additionOperators[currentToken.getValue()]?.invoke()
                    ?: return ParseResult(
                        ParserSyntaxError("Operador no válido: ${currentToken.getValue()}"),
                        stream,
                    )
            val operatorPos = currentToken.getPosition()
            val operatorPosition =
                StatementPosition(
                    operatorPos.startLine,
                    operatorPos.startColumn,
                    operatorPos.endLine,
                    operatorPos.endColumn,
                )

            val (_, streamAfterOperator) = stream.next()
            val rightResult = parseMultiplication(streamAfterOperator)

            if (rightResult.parserResult !is ValidStatementParserResult) {
                return rightResult
            }

            val rightExpression = rightResult.parserResult.getStatement() as ExpressionStatement

            // Calculate overall binary expression position
            val leftPos = getExpressionStartPosition(leftExpression)
            val rightPos = getExpressionEndPosition(rightExpression)

            val overallPosition =
                if (leftPos != null && rightPos != null) {
                    StatementPosition(
                        leftPos.startLine,
                        leftPos.startColumn,
                        rightPos.endLine,
                        rightPos.endColumn,
                    )
                } else {
                    null
                }

            // Create ComboValuePosition for operator
            val operatorCombo = ComboValuePosition(operatorValue, operatorPosition)

            val binaryExpression =
                Binary(
                    leftExpression = leftExpression,
                    operator = operatorCombo,
                    rightExpression = rightExpression,
                    position = overallPosition,
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
            val operatorValue =
                multiplicationOperators[currentToken.getValue()]?.invoke()
                    ?: return ParseResult(
                        ParserSyntaxError("Operador no válido: ${currentToken.getValue()}"),
                        stream,
                    )

            // Get operator position
            val operatorPos = currentToken.getPosition()
            val operatorPosition =
                StatementPosition(
                    operatorPos.startLine,
                    operatorPos.startColumn,
                    operatorPos.endLine,
                    operatorPos.endColumn,
                )

            val (_, streamAfterOperator) = stream.next()
            val rightResult = expressionRule.parse(streamAfterOperator)

            if (rightResult.parserResult !is ValidStatementParserResult) {
                return rightResult
            }

            val rightExpression = rightResult.parserResult.getStatement() as ExpressionStatement
            val leftPos = getExpressionStartPosition(leftExpression)
            val rightPos = getExpressionEndPosition(rightExpression)

            val overallPosition =
                if (leftPos != null && rightPos != null) {
                    StatementPosition(
                        leftPos.startLine,
                        leftPos.startColumn,
                        rightPos.endLine,
                        rightPos.endColumn,
                    )
                } else {
                    null
                }
            val operatorCombo = ComboValuePosition(operatorValue, operatorPosition)

            val binaryExpression =
                Binary(
                    leftExpression = leftExpression,
                    operator = operatorCombo,
                    rightExpression = rightExpression,
                    position = overallPosition,
                )

            parseMultiplicationContinuation(binaryExpression, rightResult.tokenStream)
        } else {
            ParseResult(ValidStatementParserResult(leftExpression), stream)
        }
    }

    private fun getExpressionStartPosition(expr: ExpressionStatement): StatementPosition? {
        return when (expr) {
            is LiteralString -> expr.value.position
            is LiteralNumber -> expr.value.position
            is LiteralBoolean -> expr.value.position
            is LiteralIdentifier -> expr.value.position
            is Binary -> expr.position ?: getExpressionStartPosition(expr.leftExpression)
            is Unary -> expr.position ?: getExpressionEndPosition(expr.value)
            else -> null
        }
    }

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
