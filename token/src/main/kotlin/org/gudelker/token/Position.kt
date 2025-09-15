package org.gudelker.token

data class Position(
    val startOffset: Int = 1,
    val endOffset: Int = 1,
    val startLine: Int = 1,
    val endLine: Int = 1,
    val startColumn: Int = 1,
    val endColumn: Int = 1,
)
