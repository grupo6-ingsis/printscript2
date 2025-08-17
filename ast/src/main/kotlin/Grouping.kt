package org.example

data class Grouping(val openParenthesis: String, val expression: ExpressionStatement,
                    val closingParenthesis: String) : ExpressionStatement {
}