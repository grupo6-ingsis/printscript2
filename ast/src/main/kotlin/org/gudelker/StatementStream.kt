package org.gudelker

import org.gudelker.statements.interfaces.Statement

class StatementStream private constructor(
    private val statements: List<Statement>,
    private val currentIndex: Int = 0,
) {
    constructor(statements: List<Statement>) : this(statements, 0)

    fun current(): Statement? = if (currentIndex < statements.size) statements[currentIndex] else null

    fun peek(offset: Int = 1): Statement? {
        val peekIndex = currentIndex + offset
        return if (peekIndex < statements.size) statements[peekIndex] else null
    }

    fun next(): Pair<Statement?, StatementStream> {
        val statement = current()
        val newIndex = if (currentIndex < statements.size) currentIndex + 1 else currentIndex
        return statement to StatementStream(statements, newIndex)
    }

    fun hasNext(): Boolean = currentIndex < statements.size

    fun isAtEnd(): Boolean = currentIndex >= statements.size
}
