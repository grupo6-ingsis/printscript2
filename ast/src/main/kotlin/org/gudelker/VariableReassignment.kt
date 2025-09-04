package org.gudelker

import org.gudelker.stmtposition.ComboValuePosition

data class VariableReassignment(
    val identifier: ComboValuePosition<String>,
    val value: ExpressionStatement,
) : VariableStatement
