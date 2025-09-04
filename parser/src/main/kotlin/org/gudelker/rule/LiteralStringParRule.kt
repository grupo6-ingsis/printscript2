package org.gudelker.rule

import org.gudelker.LiteralString
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.tokenstream.TokenStream

class LiteralStringParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.STRING
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        if (token?.getType() != TokenType.STRING) {
            val currentIndex = tokenStream.getCurrentIndex()
            return ParseResult(ParserSyntaxError("Se esperaba un string en la posición $currentIndex"), tokenStream)
        }
        val value = token.getValue()
        val tokenPosition = token.getPosition()
        val position = StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)

        val literalString = LiteralString(ComboValuePosition(value.substring(1, value.length - 1), position))

        val (_, newTokenStream) = tokenStream.consume(TokenType.STRING)
        return ParseResult(
            ValidStatementParserResult(literalString),
            newTokenStream,
        )
    }
}
