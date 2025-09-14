package org.gudelker.expressions

import org.gudelker.operators.Operator
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

data class Binary(
    val leftExpression: ExpressionStatement,
    val operator: ComboValuePosition<Operator>,
    val rightExpression: ExpressionStatement,
    val position: StatementPosition? = null,
) : ExpressionStatement
