package org.gudelker.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.Callable
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.expressions.LiteralNumber
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.tokenstream.TokenStream

class CallableParRule(private val expressionRule: SyntaxParRule) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.FUNCTION
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (functionToken, afterFunction) = tokenStream.consume(TokenType.FUNCTION)
        if (functionToken == null) {
            return ParseResult(ParserSyntaxError("Expected function name"), tokenStream)
        }
        val tokenPosition = functionToken.getPosition()
        val callablePosition =
            StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)

        val (openParenToken, afterOpenParen) = afterFunction.consume(TokenType.OPEN_PARENTHESIS)
        if (openParenToken == null) {
            return ParseResult(ParserSyntaxError("Expected '(' after function name"), afterFunction)
        }

        val expression: ExpressionStatement?
        val afterExpression: TokenStream

        if (afterOpenParen.check(TokenType.CLOSE_PARENTHESIS)) {
            expression = LiteralNumber(ComboValuePosition(0, callablePosition))
            afterExpression = afterOpenParen
        } else {
            val exprResult = expressionRule.parse(afterOpenParen)
            if (exprResult.parserResult !is ValidStatementParserResult) {
                return ParseResult(ParserSyntaxError("Invalid expression inside function call"), exprResult.tokenStream)
            }
            expression = exprResult.parserResult.getStatement() as ExpressionStatement
            afterExpression = exprResult.tokenStream
        }

        val (closeParenToken, afterCloseParen) = afterExpression.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParenToken == null) {
            return ParseResult(ParserSyntaxError("Expected ')' after function arguments"), afterExpression)
        }

        val (semicolonToken, afterSemicolon) = afterCloseParen.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return ParseResult(ParserSyntaxError("Expected ';' after function call"), afterCloseParen)
        }

//        val values = CallableAnalyzer(expressionRule).parse(tokenStream)
//        if (values !is Map<*, *>) {
//            return values as ParseResult
//        }
//        val afterSemicolon = values["afterSemicolon"] as TokenStream
//        val expression = values["expression"] as ExpressionStatement
//        val functionToken = values["functionToken"] as org.gudelker.Token
//        val callablePosition = values["callablePosition"] as StatementPosition

        val callable = Callable(ComboValuePosition(functionToken.getValue(), callablePosition), expression)
        return ParseResult(ValidStatementParserResult(callable), afterSemicolon)
    }
}
