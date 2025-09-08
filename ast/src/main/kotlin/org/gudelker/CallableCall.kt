package org.gudelker

import org.gudelker.stmtposition.ComboValuePosition

data class CallableCall(
    val functionName: ComboValuePosition<String>,
    val expression: CanBeCallStatement,
) : CallableStatement, CanBeCallStatement
