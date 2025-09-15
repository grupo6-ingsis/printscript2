package org.gudelker.parser.parsingtoken

import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.token.Token

data class IdentifierParseResult(
    val identifier: Token?,
    val nextStream: TokenStream,
)
