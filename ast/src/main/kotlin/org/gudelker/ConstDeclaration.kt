package org.gudelker

import org.gudelker.stmtposition.ComboValuePosition

data class ConstDeclaration(
    val keywordCombo: ComboValuePosition<String>,
    val identifier: String,
    val type: String?,
    val value: ExpressionStatement,
) : ConstStatement
