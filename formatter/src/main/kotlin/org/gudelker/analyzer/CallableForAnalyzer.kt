package org.gudelker.analyzer

import org.gudelker.expressions.Callable
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.interfaces.Statement
import org.gudelker.utils.FormatterUtils

class CallableForAnalyzer(private val ruleValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Callable
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is Callable) {
            return ""
        }
        val name = getFunctionName(statement)
        val functionPosition = getFunctionPosition(statement)

        val spacesBeforeName = FormatterUtils.generateSpaces(functionPosition.startColumn - 1)

        val formattedExpression = formatter.format(statement.expression, formatterRuleMap)

        var resultString = "$spacesBeforeName$name($formattedExpression);"

        ruleValidators.forEach { rule ->
            if (rule.matches(formatterRuleMap)) {
                resultString = rule.applyRule(resultString, statement, formatterRuleMap)
            }
        }

        return resultString
    }

    private fun getFunctionPosition(statement: Callable) = statement.functionName.position

    private fun getFunctionName(statement: Callable) = statement.functionName.value
}
