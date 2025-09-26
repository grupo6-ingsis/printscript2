package org.gudelker.parser.rule

import org.gudelker.expressions.LiteralString
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class LiteralStringParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return isStringToken(tokenStream)
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        if (!isStringToken(tokenStream)) {
            return errorResult(tokenStream, "Se esperaba un string en la posición ${tokenStream.getCurrentIndex()}")
        }
        return parseStringToken(tokenStream)
    }

    private fun isStringToken(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.STRING
    }

    private fun parseStringToken(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()!!
        val value = extractStringValue(token)
        val statementPosition = ParserUtils.createStatementPosition(token)
        val literalString = LiteralString(ComboValuePosition(value, statementPosition))
        val (_, newTokenStream) = tokenStream.consume(TokenType.STRING)
        return ParseResult(
            ValidStatementParserResult(literalString),
            newTokenStream,
        )
    }

    private fun extractStringValue(token: Token): String {
        val raw = token.getValue()
        return raw.substring(1, raw.length - 1)
    }

    private fun errorResult(
        tokenStream: TokenStream,
        message: String,
    ): ParseResult {
        return ParseResult(ParserSyntaxError(message), tokenStream)
    }
}
