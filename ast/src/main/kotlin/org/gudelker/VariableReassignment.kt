package org.gudelker

data class VariableReassignment(
    val identifier: String,
    val value: ExpressionStatement,
) : VariableStatement
