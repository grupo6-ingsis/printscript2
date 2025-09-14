package org.gudelker.parser.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.expressions.Grouping
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream

class GroupingParRule(private val expressionRule: SyntaxParRule) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.OPEN_PARENTHESIS
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (openParen, afterOpen) = tokenStream.consume(TokenType.OPEN_PARENTHESIS)

        if (openParen == null) {
            return ParseResult(ParserSyntaxError("Expected '('"), tokenStream)
        }

        val expressionResult = expressionRule.parse(afterOpen)
        val newTokenStream = expressionResult.tokenStream

        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return ParseResult(ParserSyntaxError("Invalid expression in grouping"), newTokenStream)
        }

        val expression = expressionResult.parserResult.getStatement()
        if (expression !is ExpressionStatement) {
            return ParseResult(ParserSyntaxError(""), newTokenStream)
        }
        val (closeParen, afterClose) = newTokenStream.consume(TokenType.CLOSE_PARENTHESIS)

        if (closeParen == null) {
            return ParseResult(ParserSyntaxError("Expected ')'"), newTokenStream)
        }

        val grouping = Grouping(openParen.getValue(), expression, closeParen.getValue())
        return ParseResult(ValidStatementParserResult(grouping), afterClose)
    }
}
