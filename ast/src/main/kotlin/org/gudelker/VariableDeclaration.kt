package org.gudelker

data class VariableDeclaration(
    val keyword: String,
    val identifier: String,
    val type: String?,
    val value: ExpressionStatement,
) : VariableStatement
