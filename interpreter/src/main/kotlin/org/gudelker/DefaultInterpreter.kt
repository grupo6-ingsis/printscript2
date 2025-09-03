package org.gudelker

import org.gudelker.evaluator.Analyzer
import org.gudelker.evaluator.Evaluator
import org.gudelker.evaluator.VariableContext

class DefaultInterpreter(private val list: List<Any?>, private val evaluators: List<Evaluator<out Any>>) : Interpreter {
    override fun interpret(statements: List<Statement>): List<Any?> {
        return recursiveInterpret(statements, list, VariableContext())
    }

    private fun recursiveInterpret(
        statements: List<Statement>,
        interpretList: List<Any?>,
        context: VariableContext,
    ): List<Any?> {
        if (statements.isEmpty()) return interpretList
        val statement = statements.first()
        val result = Analyzer.analyze(statement, context, evaluators)
        val newList = interpretList + result.value
        return recursiveInterpret(
            statements.subList(1, statements.size),
            newList,
            result.context,
        )
    }
}
