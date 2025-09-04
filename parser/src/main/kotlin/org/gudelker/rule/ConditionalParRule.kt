package org.gudelker.rule

import org.gudelker.result.ParseResult
import org.gudelker.tokenstream.TokenStream

class ConditionalParRule : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        TODO()
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val ifToken = tokenStream.current()
        TODO()
    }
}
