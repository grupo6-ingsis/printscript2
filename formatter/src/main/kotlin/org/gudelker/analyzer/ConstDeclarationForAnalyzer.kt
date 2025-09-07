package org.gudelker.analyzer

import org.gudelker.ConstDeclaration
import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter

class ConstDeclarationForAnalyzer(private val rulesValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is ConstDeclaration
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val constDeclaration = statement as ConstDeclaration

        val keyword = constDeclaration.keywordCombo.value
        val identifier = constDeclaration.identifierCombo.value
        val typeStr = constDeclaration.type
        val valueFormatted = formatter.formatNode(constDeclaration.value, formatterRuleMap)

        var string =
            if (typeStr == null) {
                "$keyword $identifier=$valueFormatted;"
            } else {
                "$keyword $identifier:$typeStr=$valueFormatted;"
            }

        rulesValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                string = validator.applyRule(string, statement, formatterRuleMap)
            }
        }

        return string
    }
}
