package org.gudelker.smtposition

data class StatementPosition(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int,
)
