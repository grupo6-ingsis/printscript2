package org.gudelker.rule

import org.gudelker.Callable
import org.gudelker.ExpressionStatement
import org.gudelker.Grouping
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.SyntaxError
import org.gudelker.result.ValidStatementResult
import org.gudelker.tokenstream.TokenStream

class CallableRule(private val expressionRule: SyntaxRule) : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.FUNCTION
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (functionToken, afterFunction) = tokenStream.consume(TokenType.FUNCTION)
        if (functionToken == null) {
            return ParseResult(SyntaxError("Expected function name"), tokenStream)
        }

        val (openParenToken, afterOpenParen) = afterFunction.consume(TokenType.OPEN_PARENTHESIS)
        if (openParenToken == null) {
            return ParseResult(SyntaxError("Expected '(' after function name"), afterFunction)
        }

        if (afterOpenParen.check(TokenType.CLOSE_PARENTHESIS)) {
            val (closeParenToken, afterCloseParen) = afterOpenParen.consume(TokenType.CLOSE_PARENTHESIS)
            val grouping =
                Grouping(
                    openParenToken.getValue(),
                    null,
                    closeParenToken?.getValue() ?: "",
                )
            val callable = Callable(functionToken.getValue(), grouping)
            return ParseResult(ValidStatementResult(callable), afterCloseParen)
        }

        val exprResult = expressionRule.parse(afterOpenParen)
        if (exprResult.result !is ValidStatementResult) {
            return ParseResult(SyntaxError("Invalid expression inside function call"), exprResult.tokenStream)
        }
        val expr = exprResult.result.getStatement() as ExpressionStatement

        val (closeParenToken, afterCloseParen) = exprResult.tokenStream.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParenToken == null) {
            return ParseResult(SyntaxError("Expected ')' after function arguments"), exprResult.tokenStream)
        }

        val grouping = Grouping(openParenToken.getValue(), expr, closeParenToken.getValue())
        val callable = Callable(functionToken.getValue(), grouping)
        return ParseResult(ValidStatementResult(callable), afterCloseParen)
    }
}
