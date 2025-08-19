package org.example.org.gudelker

import org.gudelker.operator.Operator

data class Binary(
  val leftExpression: ExpressionStatement,
  val operator: Operator,
  val rightExpression: ExpressionStatement,
) : ExpressionStatement
