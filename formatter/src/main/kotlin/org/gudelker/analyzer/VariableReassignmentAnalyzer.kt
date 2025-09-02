package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.VariableReassignment
import org.gudelker.rules.Rule
import org.gudelker.rulevalidator.RuleValidatorFormatter

class VariableReassignmentAnalyzer(
    private val ruleValidators: List<RuleValidatorFormatter>,
) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableReassignment
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        val reassignment = statement as VariableReassignment
        val identifier = reassignment.identifier
        val value = formatter.format(reassignment.value, ruleMap)
        var string = "$identifier=$value;"
        ruleValidators.forEach { validator ->
            if (validator.matches(ruleMap)) {
                string = validator.applyRule(string, statement, ruleMap)
            }
        }

        return string
    }
}
