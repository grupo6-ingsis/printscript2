package org.gudelker.result

import org.gudelker.Position

data class LexerError(val errMessage: String, val position: Position) : TokenResult
