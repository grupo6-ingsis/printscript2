package org.gudelker.parser.parsingtoken

import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.stmtposition.StatementPosition

data class FunctionCallParseResult(
    val expression: CanBeCallStatement?,
    val nextStream: TokenStream,
    val position: StatementPosition,
)
