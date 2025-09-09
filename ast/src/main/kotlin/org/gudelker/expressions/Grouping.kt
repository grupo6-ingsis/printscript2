package org.gudelker.expressions

data class Grouping(
    val openParenthesis: String,
    val expression: ExpressionStatement?,
    val closingParenthesis: String,
) : ExpressionStatement
