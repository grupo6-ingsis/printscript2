package org.gudelker

import org.gudelker.evaluator.Analyzer
import org.gudelker.evaluator.ConstVariableContext
import org.gudelker.evaluator.Evaluator
import org.gudelker.statements.interfaces.Statement

class DefaultInterpreter(
    private val list: List<Any?>,
    private val evaluators: List<Evaluator<out Any>>,
) : Interpreter {
    override fun interpret(statements: List<Statement>): List<Any?> {
        return recursiveInterpret(statements, list, ConstVariableContext())
    }

    private fun recursiveInterpret(
        statements: List<Statement>,
        interpretList: List<Any?>,
        context: ConstVariableContext,
    ): List<Any?> {
        if (statements.isEmpty()) return interpretList
        val statement = statements.first()
        val result = Analyzer.analyze(statement, context, evaluators)
        return if (result.isSuccess) {
            val evalResult = result.getOrThrow()
            val newList = interpretList + evalResult.value
            recursiveInterpret(
                statements.subList(1, statements.size),
                newList,
                evalResult.context,
            )
        } else {
            interpretList
        }
    }
}
