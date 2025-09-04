package org.gudelker.rule

import org.gudelker.LiteralBoolean
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.tokenstream.TokenStream

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
