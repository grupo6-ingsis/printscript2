package org.gudelker.expressions

import org.gudelker.compare.operators.Comparator
import org.gudelker.stmtposition.StatementPosition

data class BooleanExpression(
    val left: ExpressionStatement,
    val comparator: Comparator,
    val right: ExpressionStatement,
    val position: StatementPosition? = null,
) : BooleanExpressionStatement
