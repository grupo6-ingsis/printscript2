package org.gudelker.parser.rule

import org.gudelker.expressions.ExpressionStatement
import org.gudelker.expressions.Grouping
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.token.TokenType

class GroupingParRule(private val expressionRule: SyntaxParRule) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.OPEN_PARENTHESIS
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        return consumeTokens(tokenStream)
    }

    private fun consumeTokens(tokenStream: TokenStream): ParseResult {
        val (openParen, afterOpen) = tokenStream.consume(TokenType.OPEN_PARENTHESIS)
        if (openParen == null) {
            return ParseResult(ParserSyntaxError("Expected '(' at start of grouping"), tokenStream)
        }

        val expressionResult = expressionRule.parse(afterOpen)
        val newTokenStream = expressionResult.tokenStream

        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return ParseResult(ParserSyntaxError("Invalid expression inside grouping"), newTokenStream)
        }
        val expression = expressionResult.parserResult.getStatement()
        if (expression !is ExpressionStatement) {
            return ParseResult(ParserSyntaxError("Parsed statement is not an expression"), newTokenStream)
        }

        val (closeParen, afterClose) = newTokenStream.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParen == null) {
            return ParseResult(ParserSyntaxError("Expected ')' at end of grouping"), newTokenStream)
        }

        val grouping = createGrouping(openParen.getValue(), expression, closeParen.getValue())
        return ParseResult(ValidStatementParserResult(grouping), afterClose)
    }

    private fun createGrouping(
        openParenValue: String,
        expression: ExpressionStatement?,
        closeParenValue: String,
    ): Grouping {
        return Grouping(openParenValue, expression, closeParenValue)
    }
}
