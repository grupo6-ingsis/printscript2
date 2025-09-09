package org.gudelker.expressions

import org.gudelker.statements.interfaces.CallableStatement
import org.gudelker.stmtposition.ComboValuePosition

data class CallableCall(
    val functionName: ComboValuePosition<String>,
    val expression: CanBeCallStatement,
) : CallableStatement, CanBeCallStatement
