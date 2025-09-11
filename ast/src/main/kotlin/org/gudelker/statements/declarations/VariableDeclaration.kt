package org.gudelker.statements.declarations

import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.statements.interfaces.VariableStatement
import org.gudelker.stmtposition.ComboValuePosition

data class VariableDeclaration(
    val keywordCombo: ComboValuePosition<String>,
    val identifierCombo: ComboValuePosition<String>,
    val type: String?,
    val value: CanBeCallStatement?,
) : VariableStatement
