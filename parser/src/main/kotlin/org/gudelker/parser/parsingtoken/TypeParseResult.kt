package org.gudelker.parser.parsingtoken

import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.StatementPosition

data class TypeParseResult(
    val colon: String?,
    val colonPosition: StatementPosition?,
    val typeName: String?,
    val typePosition: StatementPosition?,
    val error: ParserSyntaxError?,
    val nextStream: TokenStream,
)
