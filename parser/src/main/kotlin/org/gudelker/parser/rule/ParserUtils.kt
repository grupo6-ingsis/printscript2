package org.gudelker.parser.rule

import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.expressions.LiteralNumber
import org.gudelker.parser.parsingtoken.FunctionCallHeaderParseResult
import org.gudelker.parser.parsingtoken.FunctionCallParseResult
import org.gudelker.parser.parsingtoken.IdentifierParseResult
import org.gudelker.parser.parsingtoken.TypeParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

object ParserUtils {
    fun parseIdentifier(stream: TokenStream): IdentifierParseResult {
        val (identifierToken, nextStream) = stream.consume(TokenType.IDENTIFIER)
        return IdentifierParseResult(identifierToken, nextStream)
    }

    fun parseOptionalType(stream: TokenStream): TypeParseResult {
        if (stream.check(TokenType.COLON)) {
            val (colonToken, streamAfterColon) = stream.consume(TokenType.COLON)
            val colonPos =
                StatementPosition(
                    colonToken!!.getPosition().startLine,
                    colonToken.getPosition().startColumn,
                    colonToken.getPosition().endLine,
                    colonToken.getPosition().endColumn,
                )
            val (typeToken, streamAfterType) = streamAfterColon.consume(TokenType.TYPE)
            val typePos =
                typeToken?.let {
                    StatementPosition(
                        it.getPosition().startLine,
                        it.getPosition().startColumn,
                        it.getPosition().endLine,
                        it.getPosition().endColumn,
                    )
                }
            return if (typeToken == null) {
                TypeParseResult(null, null, null, null, ParserSyntaxError("Se esperaba un tipo después de ':'"), streamAfterColon)
            } else {
                TypeParseResult(":", colonPos, typeToken.getValue(), typePos, null, streamAfterType)
            }
        }
        return TypeParseResult(null, null, null, null, null, stream)
    }

    fun parseFunctionCallArguments(
        functionToken: Token,
        afterFunction: TokenStream,
        expressionRule: SyntaxParRule,
    ): FunctionCallParseResult {
        val tokenPosition = functionToken.getPosition()
        val callablePosition =
            StatementPosition(
                tokenPosition.startLine,
                tokenPosition.startColumn,
                tokenPosition.endLine,
                tokenPosition.endColumn,
            )
        val (openParenToken, afterOpenParen) = afterFunction.consume(TokenType.OPEN_PARENTHESIS)
        if (openParenToken == null) {
            return FunctionCallParseResult(null, afterFunction, callablePosition)
        }
        val expression: CanBeCallStatement?
        val afterExpression: TokenStream
        if (afterOpenParen.check(TokenType.CLOSE_PARENTHESIS)) {
            expression = LiteralNumber(ComboValuePosition(0, callablePosition))
            afterExpression = afterOpenParen
        } else {
            val exprResult = expressionRule.parse(afterOpenParen)
            if (exprResult.parserResult !is ValidStatementParserResult) {
                return FunctionCallParseResult(null, exprResult.tokenStream, callablePosition)
            }
            expression = exprResult.parserResult.getStatement() as CanBeCallStatement
            afterExpression = exprResult.tokenStream
        }
        return FunctionCallParseResult(expression, afterExpression, callablePosition)
    }

    fun parseFunctionCallHeader(
        tokenStream: TokenStream,
        expressionRule: SyntaxParRule,
    ): FunctionCallHeaderParseResult {
        val (functionToken, afterFunction) = tokenStream.consume(TokenType.FUNCTION)
        if (functionToken == null) {
            return FunctionCallHeaderParseResult(
                null,
                null,
                StatementPosition(0, 0, 0, 0),
                tokenStream,
                ParserSyntaxError("Expected function name"),
            )
        }
        val result = parseFunctionCallArguments(functionToken, afterFunction, expressionRule)
        if (result.expression == null) {
            return FunctionCallHeaderParseResult(
                functionToken,
                null,
                result.position,
                result.nextStream,
                ParserSyntaxError("Invalid expression inside function call"),
            )
        }
        val (closeParenToken, afterCloseParen) = result.nextStream.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParenToken == null) {
            return FunctionCallHeaderParseResult(
                functionToken,
                result.expression,
                result.position,
                result.nextStream,
                ParserSyntaxError("Expected ')' after function arguments"),
            )
        }
        return FunctionCallHeaderParseResult(functionToken, result.expression, result.position, afterCloseParen, null)
    }
}
