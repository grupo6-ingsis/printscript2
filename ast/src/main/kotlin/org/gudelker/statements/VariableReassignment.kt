package org.gudelker.statements

import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.statements.interfaces.VariableStatement
import org.gudelker.stmtposition.ComboValuePosition

data class VariableReassignment(
    val identifier: ComboValuePosition<String>,
    val value: CanBeCallStatement,
) : VariableStatement
