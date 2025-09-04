package org.gudelker.rule

import org.gudelker.ExpressionStatement
import org.gudelker.Grouping
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.tokenstream.TokenStream

class GroupingParRule(private val expressionRule: SyntaxParRule) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.OPEN_PARENTHESIS
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (openParen, afterOpen) = tokenStream.consume(TokenType.OPEN_PARENTHESIS)
        if (openParen == null) {
            return ParseResult(ParserSyntaxError("Expected '('"), tokenStream)
        }

        val exprResult = expressionRule.parse(afterOpen)
        if (exprResult.parserResult !is ValidStatementParserResult) {
            return ParseResult(ParserSyntaxError("Invalid expression in grouping"), exprResult.tokenStream)
        }
        val expr = exprResult.parserResult.getStatement() as ExpressionStatement

        val (closeParen, afterClose) = exprResult.tokenStream.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParen == null) {
            return ParseResult(ParserSyntaxError("Expected ')'"), exprResult.tokenStream)
        }

        val grouping = Grouping(openParen.getValue(), expr, closeParen.getValue())
        return ParseResult(ValidStatementParserResult(grouping), afterClose)
    }
}
