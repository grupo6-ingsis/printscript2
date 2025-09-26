package org.gudelker.parser.rule

import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.token.TokenType

class LiteralIdentifierParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return isIdentifierToken(tokenStream)
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        if (!isIdentifierToken(tokenStream)) {
            return errorResult(tokenStream, "Se esperaba un identifier en la posición ${tokenStream.getCurrentIndex()}")
        }
        return parseIdentifierToken(tokenStream)
    }

    private fun isIdentifierToken(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.IDENTIFIER
    }

    private fun parseIdentifierToken(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()!!
        val value = token.getValue()
        val position = ParserUtils.createStatementPosition(token)
        val literalIdentifier = LiteralIdentifier(ComboValuePosition(value, position))
        val (_, newTokenStream) = tokenStream.consume(TokenType.IDENTIFIER)
        return ParseResult(
            ValidStatementParserResult(literalIdentifier),
            newTokenStream,
        )
    }

    private fun errorResult(
        tokenStream: TokenStream,
        message: String,
    ): ParseResult {
        return ParseResult(ParserSyntaxError(message), tokenStream)
    }
}
