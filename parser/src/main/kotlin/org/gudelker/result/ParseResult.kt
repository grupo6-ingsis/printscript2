package org.gudelker.result

import org.gudelker.tokenstream.TokenStream

data class ParseResult(
    val parserResult: ParserResult,
    val tokenStream: TokenStream,
)
