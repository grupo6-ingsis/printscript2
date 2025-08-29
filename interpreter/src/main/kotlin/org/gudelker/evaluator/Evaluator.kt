package org.gudelker.evaluator

import org.gudelker.Statement

sealed interface Evaluator<T> {
    fun evaluate(statement: Statement): T
}
