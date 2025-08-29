package org.gudelker.rule

import org.gudelker.LiteralString
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.tokenstream.TokenStream

class LiteralStringRule : SyntaxRule {
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
        val literalString = LiteralString(value.substring(1, value.length - 1))

        val (type, tokenStream) = tokenStream.consume(TokenType.STRING)
        return ParseResult(
            ValidStatementParserResult(literalString),
            tokenStream,
        )
    }
}
