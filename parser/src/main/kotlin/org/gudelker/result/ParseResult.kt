package org.gudelker.result

import org.gudelker.tokenstream.TokenStream

data class ParseResult(
    val result: Result,
    val tokenStream: TokenStream,
)
