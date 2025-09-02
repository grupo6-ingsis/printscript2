package org.gudelker.analyzer

import org.gudelker.Callable
import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.rules.Rule
import org.gudelker.rulevalidator.RuleValidatorFormatter

class CallableAnalyzer(private val ruleValidators: List<RuleValidatorFormatter>) : Analyzer {
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
        var string = "$name($formattedExpression);"

        ruleValidators.forEach { rule ->
            if (rule.matches(ruleMap)) {
                string = rule.applyRule(string, statement, ruleMap)
            }
        }

        return string
    }
}
