package org.gudelker.parser.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.LiteralString
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

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
        val statementPosition =
            StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)
        val literalString = LiteralString(ComboValuePosition(value.substring(1, value.length - 1), statementPosition))
        val (_, newTokenStream) = tokenStream.consume(TokenType.STRING)
        return ParseResult(
            ValidStatementParserResult(literalString),
            newTokenStream,
        )
    }
}
