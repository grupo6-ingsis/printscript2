package org.gudelker.rule

import org.gudelker.result.ParseResult
import org.gudelker.tokenstream.TokenStream

interface SyntaxParRule {
    fun matches(tokenStream: TokenStream): Boolean

    fun parse(tokenStream: TokenStream): ParseResult
}
