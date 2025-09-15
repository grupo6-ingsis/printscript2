package org.gudelker.parser.rule

import org.gudelker.expressions.CallableCall
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.token.TokenType

class CallableCallParRule(private val expressionRule: SyntaxParRule) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.FUNCTION
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val header = ParserUtils.parseFunctionCallHeader(tokenStream, expressionRule)
        if (header.error != null) {
            return errorResult(header.error.getError(), header.nextStream)
        }
        val callable =
            CallableCall(
                ComboValuePosition(header.functionToken!!.getValue(), header.position),
                header.expression!!,
            )
        return ParseResult(ValidStatementParserResult(callable), header.nextStream)
    }

    private fun errorResult(
        message: String,
        stream: TokenStream,
    ) = ParseResult(ParserSyntaxError(message), stream)
}
