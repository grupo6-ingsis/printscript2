package org.gudelker.rule

import org.gudelker.LiteralNumber
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.tokenstream.TokenStream

class LiteralNumberParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.NUMBER
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        if (token?.getType() != TokenType.NUMBER) {
            val currentIndex = tokenStream.getCurrentIndex()
            return ParseResult(ParserSyntaxError("Se esperaba un número en la posición $currentIndex"), tokenStream)
        }
        val value =
            if (token.getValue().contains(".")) {
                token.getValue().toFloat()
            } else {
                token.getValue().toInt()
            }
        val tokenPosition = token.getPosition()
        val position = StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)
        val literalNumber = LiteralNumber(ComboValuePosition(value, position))

        val (_, newTokenStream) = tokenStream.consume(TokenType.NUMBER)
        return ParseResult(
            ValidStatementParserResult(literalNumber),
            newTokenStream,
        )
    }
}
