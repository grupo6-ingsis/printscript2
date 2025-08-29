package org.gudelker.rule

import org.gudelker.LiteralIdentifier
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.tokenstream.TokenStream

class LiteralIdentifierRule : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.IDENTIFIER
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()
        val value = token?.getValue() ?: ""
        val literalIdentifier = LiteralIdentifier(value)
        val (newToken, tokenStream) = tokenStream.consume(TokenType.IDENTIFIER)
        return ParseResult(
            ValidStatementParserResult(literalIdentifier),
            tokenStream,
        )
    }
}
