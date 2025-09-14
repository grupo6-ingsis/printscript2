package org.gudelker.parser.parsingtoken

import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.tokenstream.TokenStream

data class TypeParseResult(
    val typeName: String?,
    val error: ParserSyntaxError?,
    val nextStream: TokenStream,
)
