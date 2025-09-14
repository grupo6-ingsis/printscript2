package org.gudelker.parser.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.LiteralBoolean
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

class LiteralBooleanParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.BOOLEAN
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        if (token?.getType() != TokenType.BOOLEAN) {
            val currentIndex = tokenStream.getCurrentIndex()
            return ParseResult(ParserSyntaxError("Se esperaba un boolean en la posición $currentIndex"), tokenStream)
        }
        val value = token.getValue().toBoolean()
        val tokenPosition = token.getPosition()
        val position = StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)
        val literalBoolean = LiteralBoolean(ComboValuePosition(value, position))
        val (_, newTokenStream) = tokenStream.consume(TokenType.BOOLEAN)
        return ParseResult(
            ValidStatementParserResult(literalBoolean),
            newTokenStream,
        )
    }
}
