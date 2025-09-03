package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.VariableDeclaration
import org.gudelker.rules.Rule
import org.gudelker.rulevalidator.RuleValidatorFormatter

class VariableDeclarationAnalyzer(private val rulesValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableDeclaration
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        val declaration = statement as VariableDeclaration
        val keyword = statement.keywordCombo
        val identifier = declaration.identifier
        val typeStr = statement.type
        val valueFormatted = formatter.formatNode(declaration.value, ruleMap)

        var string =
            if (typeStr == null) {
                "$keyword $identifier=$valueFormatted;"
            } else {
                "$keyword $identifier:$typeStr=$valueFormatted;"
            }

        rulesValidators.forEach { validator ->
            if (validator.matches(ruleMap)) {
                string = validator.applyRule(string, statement, ruleMap)
            }
        }

        return string
    }
}
