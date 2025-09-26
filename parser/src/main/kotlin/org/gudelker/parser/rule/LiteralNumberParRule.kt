package org.gudelker.parser.rule

import org.gudelker.expressions.LiteralNumber
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class LiteralNumberParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return isNumberToken(tokenStream)
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        if (!isNumberToken(tokenStream)) {
            return errorResult(tokenStream, "Se esperaba un número en la posición ${tokenStream.getCurrentIndex()}")
        }
        return parseNumberToken(tokenStream)
    }

    private fun isNumberToken(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.NUMBER
    }

    private fun parseNumberToken(tokenStream: TokenStream): ParseResult {
        val token = tokenStream.current()!!
        val value = getNumberValue(token)
        val position = ParserUtils.createStatementPosition(token)
        val literalNumber = LiteralNumber(ComboValuePosition(value, position))
        val (_, newTokenStream) = tokenStream.consume(TokenType.NUMBER)
        return ParseResult(
            ValidStatementParserResult(literalNumber),
            newTokenStream,
        )
    }

    private fun getNumberValue(token: Token): Number {
        return if (token.getValue().contains(".")) {
            token.getValue().toDouble()
        } else {
            token.getValue().toInt()
        }
    }

    private fun errorResult(
        tokenStream: TokenStream,
        message: String,
    ): ParseResult {
        return ParseResult(ParserSyntaxError(message), tokenStream)
    }
}
