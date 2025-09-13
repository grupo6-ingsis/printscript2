package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.expressions.Callable
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
        val formattedExpression = formatter.format(callable.expression, formatterRuleMap)
        var string = "$name($formattedExpression);\n"

        ruleValidators.forEach { rule ->
            if (rule.matches(formatterRuleMap)) {
                string = rule.applyRule(string, statement, formatterRuleMap)
            }
        }

        return string
    }
}
