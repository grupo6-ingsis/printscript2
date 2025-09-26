package org.gudelker.parser.rule

import org.gudelker.expressions.LiteralBoolean
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.token.TokenType

class LiteralBooleanParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean = checkBooleanToken(tokenStream)

    override fun parse(tokenStream: TokenStream): ParseResult = parseBooleanToken(tokenStream)

    private fun checkBooleanToken(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.BOOLEAN
    }

    private fun parseBooleanToken(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        if (token?.getType() != TokenType.BOOLEAN) {
            val currentIndex = tokenStream.getCurrentIndex()
            return ParseResult(ParserSyntaxError("Expected boolean at position $currentIndex"), tokenStream)
        }
        val value = token.getValue().toBoolean()
        val position = ParserUtils.createStatementPosition(token)
        val literalBoolean = LiteralBoolean(ComboValuePosition(value, position))
        val (_, newTokenStream) = tokenStream.consume(TokenType.BOOLEAN)
        return ParseResult(ValidStatementParserResult(literalBoolean), newTokenStream)
    }
}
