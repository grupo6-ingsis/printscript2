package org.gudelker.analyzer

import org.gudelker.Callable
import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.rules.Rule
import org.gudelker.utils.FormatterUtils

class CallableAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Callable
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        val callable = statement as Callable
        val name = callable.functionName
        val formattedExpression = formatter.format(callable.expression, ruleMap)
        val newLines = FormatterUtils.generateNewLines("println", ruleMap)
        return "$newLines$name($formattedExpression);"
    }
}
