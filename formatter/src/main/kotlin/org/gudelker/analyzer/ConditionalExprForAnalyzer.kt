package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.interfaces.Statement

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
            }.trimEnd('\n')

        var result =
            if (elseBody != null) {
                val formattedElseBody =
                    elseBody.joinToString("") {
                        formatter.format(it, formatterRuleMap)
                    }.trimEnd('\n')
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
