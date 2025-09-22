package org.gudelker.analyzer

import org.gudelker.expressions.CallableCall
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.interfaces.Statement

class CallableCallForAnalyzer(private val ruleValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is CallableCall
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val callableCall = statement as CallableCall
        val name = callableCall.functionName.value
        val formattedExpression = formatter.format(callableCall.expression, formatterRuleMap)
        var string = "$name($formattedExpression);"
        ruleValidators.forEach { rule ->
            if (rule.matches(formatterRuleMap)) {
                string = rule.applyRule(string, statement, formatterRuleMap)
            }
        }
        return string
    }
}
