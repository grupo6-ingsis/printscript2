package org.gudelker

import org.gudelker.evaluator.Analyzer

class DefaultInterpreter(private val list: List<Any?>) : Interpreter {
    override fun interpret(statements: List<Statement>): List<Any?> {
        return recursiveInterpret(statements, list)
    }

    private fun recursiveInterpret(
        statements: List<Statement>,
        interpretList: List<Any?>,
    ): List<Any?> {
        if (statements.isEmpty()) return interpretList
        val statement = statements.first()
        val result = Analyzer.analyze(statement)
        val newList = interpretList + result
        return recursiveInterpret(statements.subList(1, statements.size), newList)
    }
}
