package org.gudelker

interface Formatter {
    fun format(statements: List<Statement>): String
}
