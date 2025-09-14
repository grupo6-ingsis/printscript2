package org.gudelker.parser.parsingtoken

import org.gudelker.Token
import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.StatementPosition

data class FunctionCallHeaderParseResult(
    val functionToken: Token?,
    val expression: CanBeCallStatement?,
    val position: StatementPosition,
    val nextStream: TokenStream,
    val error: ParserSyntaxError?,
)
