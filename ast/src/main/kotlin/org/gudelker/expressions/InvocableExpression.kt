package org.gudelker.expressions

import org.gudelker.statements.interfaces.CallableStatement
import org.gudelker.stmtposition.ComboValuePosition

data class InvocableExpression(
    val functionName: ComboValuePosition<String>,
    val expression: CanBeCallStatement,
) : CallableStatement, CanBeCallStatement
