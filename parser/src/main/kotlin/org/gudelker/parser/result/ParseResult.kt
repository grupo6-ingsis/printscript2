package org.gudelker.parser.result

import org.gudelker.parser.tokenstream.TokenStream

data class ParseResult(
    val parserResult: ParserResult,
    val tokenStream: TokenStream,
)
