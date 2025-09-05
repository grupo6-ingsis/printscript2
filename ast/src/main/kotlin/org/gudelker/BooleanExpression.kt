package org.gudelker

import org.gudelker.comparator.Comparator

data class BooleanExpression(
    val left: ExpressionStatement,
    val comparator: Comparator,
    val right: ExpressionStatement,
) : BooleanExpressionStatement
