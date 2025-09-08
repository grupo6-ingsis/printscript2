package org.gudelker

import org.gudelker.stmtposition.ComboValuePosition

data class VariableDeclaration(
    val keywordCombo: ComboValuePosition<String>,
    val identifierCombo: ComboValuePosition<String>,
    val type: String?,
    val value: CanBeCallStatement,
) : VariableStatement
