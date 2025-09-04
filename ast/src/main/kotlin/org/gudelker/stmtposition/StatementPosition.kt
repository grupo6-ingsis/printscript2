package org.gudelker.stmtposition

data class StatementPosition(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int,
)
