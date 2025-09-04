package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.VariableReassignment
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter

class VariableReassignmentForAnalyzer(
    private val ruleValidators: List<RuleValidatorFormatter>,
) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableReassignment
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val reassignment = statement as VariableReassignment
        val identifier = reassignment.identifier
        val value = formatter.format(reassignment.value, formatterRuleMap)
        var string = "$identifier=$value;"
        ruleValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                string = validator.applyRule(string, statement, formatterRuleMap)
            }
        }

        return string
    }
}
