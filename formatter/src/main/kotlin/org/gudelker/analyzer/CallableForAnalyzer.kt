package org.gudelker.analyzer

import org.gudelker.expressions.Callable
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.interfaces.Statement

class CallableForAnalyzer(private val ruleValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Callable
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val callable = statement as Callable
        val name = callable.functionName.value
        val position = callable.functionName.position
        val spacesBeforeName = " ".repeat(position.startColumn - 1)

        val formattedExpression = formatter.format(callable.expression, formatterRuleMap)
        var string = "$spacesBeforeName$name($formattedExpression);"

        ruleValidators.forEach { rule ->
            if (rule.matches(formatterRuleMap)) {
                string = rule.applyRule(string, statement, formatterRuleMap)
            }
        }

        return string
    }
}
