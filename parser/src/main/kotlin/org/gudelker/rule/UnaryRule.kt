package org.gudelker.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.tokenstream.TokenStream

class UnaryRule : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.OPERATOR
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        TODO("Not yet implemented")
    }
}
