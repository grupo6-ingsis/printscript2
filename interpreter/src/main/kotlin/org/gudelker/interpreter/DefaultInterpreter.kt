package org.gudelker.interpreter

import org.gudelker.evaluator.Analyzer
import org.gudelker.evaluator.ConstVariableContext
import org.gudelker.evaluator.Evaluator
import org.gudelker.statements.interfaces.Statement

class DefaultInterpreter(
    private val list: List<Any?>,
    private val evaluators: List<Evaluator<out Any>>,
) : Interpreter {
    override fun interpret(statements: List<Statement>): Result<List<Any?>> {
        return recursiveInterpret(statements, list, ConstVariableContext())
    }

    private fun recursiveInterpret(
        statements: List<Statement>,
        interpretList: List<Any?>,
        context: ConstVariableContext,
    ): Result<List<Any?>> {
        if (statements.isEmpty()) return Result.success(interpretList)
        val statement = statements.first()
        val result = Analyzer.analyze(statement, context, evaluators)
        if (result.isSuccess) {
            val evalResult = result.getOrThrow()
            val newList = interpretList + evalResult.value
            return recursiveInterpret(
                statements.subList(1, statements.size),
                newList,
                evalResult.context,
            )
        } else {
            return Result.failure(Exception("There was an invalid statement"))
        }
    }
}
