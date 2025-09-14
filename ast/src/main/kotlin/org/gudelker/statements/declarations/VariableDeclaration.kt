package org.gudelker.statements.declarations

import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.statements.interfaces.VariableStatement
import org.gudelker.stmtposition.ComboValuePosition

data class VariableDeclaration(
    val keywordCombo: ComboValuePosition<String>,
    val identifierCombo: ComboValuePosition<String>,
    val colon: ComboValuePosition<String>?,
    val type: ComboValuePosition<String>?,
    val equals: ComboValuePosition<String>?,
    val value: CanBeCallStatement?,
) : VariableStatement

