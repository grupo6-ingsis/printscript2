package org.gudelker.parser.rule

import org.gudelker.expressions.LiteralNumber
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.token.Token
import org.gudelker.token.TokenType

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
        val value = getNumberValue(token)
        val tokenPosition = token.getPosition()
        val position = StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)
        val literalNumber = LiteralNumber(ComboValuePosition(value, position))
        val (_, newTokenStream) = tokenStream.consume(TokenType.NUMBER)
        return ParseResult(
            ValidStatementParserResult(literalNumber),
            newTokenStream,
        )
    }

    private fun getNumberValue(token: Token): Number {
        val value =
            if (token.getValue().contains(".")) {
                token.getValue().toFloat()
            } else {
                token.getValue().toInt()
            }
        return value
    }
}
