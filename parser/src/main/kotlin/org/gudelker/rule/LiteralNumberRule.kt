package org.gudelker.rule

import org.example.org.gudelker.LiteralNumber
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.SyntaxError
import org.gudelker.result.ValidStatementResult
import org.gudelker.tokenstream.TokenStream

class LiteralNumberRule : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.NUMBER
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        if (token?.getType() != TokenType.NUMBER) {
            val currentIndex = tokenStream.getCurrentIndex()
            return ParseResult(SyntaxError("Se esperaba un número en la posición $currentIndex"), tokenStream)
        }
        val value =
            if (token.getValue().contains(".")) {
                token.getValue().toFloat()
            } else {
                token.getValue().toInt()
            }

        val literalNumber = LiteralNumber(value)

        val (type, tokenStream) = tokenStream.consume(TokenType.NUMBER)
        return ParseResult(
            ValidStatementResult(literalNumber),
            tokenStream,
        )
    }
}
