package org.gudelker.parser.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

class LiteralIdentifierParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.IDENTIFIER
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        if (token?.getType() != TokenType.IDENTIFIER) {
            val currentIndex = tokenStream.getCurrentIndex()
            return ParseResult(ParserSyntaxError("Se esperaba un identifier en la posición $currentIndex"), tokenStream)
        }
        val value = token.getValue()
        val tokenPosition = token.getPosition()
        val position = StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)
        val literalIdentifier = LiteralIdentifier(ComboValuePosition(value, position))
        val (_, newTokenStream) = tokenStream.consume(TokenType.IDENTIFIER)
        return ParseResult(
            ValidStatementParserResult(literalIdentifier),
            newTokenStream,
        )
    }
}
