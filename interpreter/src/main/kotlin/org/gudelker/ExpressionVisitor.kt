package org.gudelker

interface ExpressionVisitor {
    fun evaluate(expression: ExpressionStatement): Any?
}
