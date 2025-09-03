package org.gudelker

import org.gudelker.smtposition.ComboValuePosition

data class VariableDeclaration(
    val keywordCombo: ComboValuePosition<String>,
    val identifier: String,
    val type: String?,
    val value: ExpressionStatement,
) : VariableStatement
