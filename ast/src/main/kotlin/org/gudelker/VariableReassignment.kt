package org.gudelker

import org.gudelker.smtposition.ComboValuePosition

data class VariableReassignment(
    val identifier: ComboValuePosition<String>,
    val value: ExpressionStatement,
) : VariableStatement
