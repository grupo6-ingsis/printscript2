package org.gudelker.rule

import org.gudelker.result.ParseResult
import org.gudelker.tokenstream.TokenStream

interface SyntaxRule {
    fun matches(tokenStream: TokenStream): Boolean

    fun parse(tokenStream: TokenStream): ParseResult
}
