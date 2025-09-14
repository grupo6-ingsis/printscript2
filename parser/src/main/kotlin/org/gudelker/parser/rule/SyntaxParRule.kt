package org.gudelker.parser.rule

import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.tokenstream.TokenStream

interface SyntaxParRule {
    fun matches(tokenStream: TokenStream): Boolean

    fun parse(tokenStream: TokenStream): ParseResult
}
