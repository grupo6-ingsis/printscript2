package org.gudelker.rule

import org.gudelker.LiteralIdentifier
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.smtposition.ComboValuePosition
import org.gudelker.smtposition.StatementPosition
import org.gudelker.tokenstream.TokenStream

class LiteralIdentifierRule : SyntaxRule {
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
        val (newToken, tokenStream) = tokenStream.consume(TokenType.IDENTIFIER)
        return ParseResult(
            ValidStatementParserResult(literalIdentifier),
            tokenStream,
        )
    }
}
