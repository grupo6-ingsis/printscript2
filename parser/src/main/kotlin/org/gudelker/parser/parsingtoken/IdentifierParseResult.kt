package org.gudelker.parser.parsingtoken

import org.gudelker.Token
import org.gudelker.parser.tokenstream.TokenStream

data class IdentifierParseResult(
    val identifier: Token?,
    val nextStream: TokenStream,
)
