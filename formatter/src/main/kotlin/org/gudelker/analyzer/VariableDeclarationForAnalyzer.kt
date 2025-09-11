package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.statements.interfaces.Statement

class VariableDeclarationForAnalyzer(private val rulesValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableDeclaration
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val declaration = statement as VariableDeclaration
        val keyword = statement.keywordCombo
        val identifier = declaration.identifierCombo.value
        val typeStr = statement.type
        val valueFormatted = formatter.formatNode(declaration.value!!, formatterRuleMap)

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
