package org.gudelker.analyzer

import org.gudelker.ConditionalExpression
import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter

class ConditionalExprForAnalyzer(
    private val ruleValidators: List<RuleValidatorFormatter>,
) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is ConditionalExpression
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val conditionalExpression = statement as ConditionalExpression
        val ifKeyword = conditionalExpression.ifKeyword.value
        val condition = conditionalExpression.condition
        val ifBody = conditionalExpression.ifBody
        val elseBody = conditionalExpression.elseBody

        val formattedCondition = formatter.format(condition, formatterRuleMap)

        val formattedIfBody =
            ifBody.joinToString("") {
                formatter.format(it, formatterRuleMap)
            }

        var result =
            if (elseBody != null) {
                val formattedElseBody =
                    elseBody.joinToString("") {
                        formatter.format(it, formatterRuleMap)
                    }
                "$ifKeyword ($formattedCondition) {\n$formattedIfBody\n} else {\n$formattedElseBody\n}"
            } else {
                "$ifKeyword ($formattedCondition) {\n$formattedIfBody\n}"
            }

        ruleValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                result = validator.applyRule(result, statement, formatterRuleMap)
            }
        }

        return result
    }
}
