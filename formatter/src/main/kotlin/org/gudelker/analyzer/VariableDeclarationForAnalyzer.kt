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
        if(statement !is VariableDeclaration) return ""
        val keyword = statement.keywordCombo
        val identifier = statement.identifierCombo.value
        val colon = statement.colon?.value
        val spacesBeforeColon = statement.colon!!.position.startColumn - statement.identifierCombo.position.endColumn
        val spacesAfterColon = statement.type!!.position.startColumn - statement.colon!!.position.endColumn
        val typeStr = statement.type?.value
        val equals = statement.equals?.value
        val valueFormatted = formatter.formatNode(statement.value!!, formatterRuleMap)
        val spacesBeforeEquals = statement.type?.position?.startColumn - statement.equals?.position?.startColumn
        // let x:x = value;

        var string =
            if (typeStr == null) {
                "$keyword $identifier$spacesBeforeEquals=$spacesAfterEquals$valueFormatted;\n"
            }
            else if (equals == null) {
                "$keyword $identifier$spacesBeforeColon:$spacesAfterColon$typeStr;\n"
            }
            else {
                "$keyword $identifier$spacesBeforeColon:$spacesAfterColon$typeStr$spacesBeforeEquals=$spacesAfterEquals$valueFormatted;\n"
            }


        rulesValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                string = validator.applyRule(string, statement, formatterRuleMap)
            }
        }

        return string
    }
}
