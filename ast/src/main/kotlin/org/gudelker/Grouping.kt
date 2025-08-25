package org.example.org.gudelker

data class Grouping(
    val openParenthesis: String,
    val expression: ExpressionStatement?,
    val closingParenthesis: String,
) : ExpressionStatement
