package org.gudelker

import org.gudelker.comparator.Comparator

data class BooleanExpression(
    val left: ExpressionStatement,
    val operator: Comparator,
    val right: ExpressionStatement,
) : BooleanExpressionStatement
