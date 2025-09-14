package org.gudelker.expressions

import org.gudelker.operators.Operator
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

data class Unary(
    val value: ExpressionStatement,
    val operator: ComboValuePosition<Operator>,
    val position: StatementPosition? = null,
) : ExpressionStatement
