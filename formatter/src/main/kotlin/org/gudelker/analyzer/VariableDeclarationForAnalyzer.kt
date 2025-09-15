package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.expressions.Binary
import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.expressions.LiteralBoolean
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.expressions.Unary
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition

class VariableDeclarationForAnalyzer(private val rulesValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableDeclaration
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        // let x
        if (statement is VariableDeclaration) {
            val keyword = statement.keywordCombo.value
            val identifier = statement.identifierCombo.value
            val beforeKeyword = statement.keywordCombo.position
            val spacesBeforeKeyword = " ".repeat(beforeKeyword.startColumn - 1)
            var resultString = "$spacesBeforeKeyword$keyword $identifier"
            if (statement.colon != null) {
                val numberOfSpacesBeforeColon =
                    statement.colon!!.position.startColumn - statement.identifierCombo.position.startColumn
                val spacesBeforeColon = " ".repeat(numberOfSpacesBeforeColon - 1)
                val numberOfSpacesAfterColon = statement.type?.position!!.startColumn - statement.colon!!.position.startColumn
                val spacesAfterColon = " ".repeat(numberOfSpacesAfterColon - 1)
                resultString += "$spacesBeforeColon${statement.colon!!.value}$spacesAfterColon${statement.type!!.value}"
            }

            if (statement.equals != null) {
                val numberOfSpacesBeforeEquals = statement.equals!!.position.startColumn - statement.type!!.position.startColumn
                val spacesBeforeEquals = " ".repeat(numberOfSpacesBeforeEquals - 1)
                val numberOfSpacesAfterEquals = calculateSpacesAfterEquals(statement.value!!, statement.equals!!)
                val spacesAfterEquals = " ".repeat(numberOfSpacesAfterEquals - 1)

                val valueFormatted = formatter.formatNode(statement.value!!, formatterRuleMap)
                resultString += "$spacesBeforeEquals${statement.equals!!.value}$spacesAfterEquals$valueFormatted"
            }
            resultString += ";"
            rulesValidators.forEach { validator ->
                if (validator.matches(formatterRuleMap)) {
                    resultString = validator.applyRule(resultString, statement, formatterRuleMap)
                }
            }

            return resultString
        }
        return ""
    }

    private fun calculateSpacesAfterEquals(
        value: CanBeCallStatement,
        equals: ComboValuePosition<String>,
    ): Int {
        return when (value) {
            is LiteralBoolean -> {
                val valuePos = value.value.position
                valuePos.startColumn - equals.position.startColumn
            }
            is LiteralNumber -> {
                val valuePos = value.value.position
                valuePos.startColumn - equals.position.endColumn
            }
            is LiteralString -> {
                val valuePos = value.value.position
                valuePos.startColumn - equals.position.endColumn
            }
            is LiteralIdentifier -> {
                val valuePos = value.value.position
                valuePos.startColumn - equals.position.endColumn
            }
            is Binary -> {
                val valuePos = value.position
                valuePos!!.startColumn - equals.position.endColumn
            }
            is Unary -> {
                val valuePos = value.operator.position
                valuePos.startColumn - equals.position.startColumn
            }
//            is Grouping -> {
//                val valuePos = value.openParenthesis
//                valuePos!!.startColumn - equals.position.endColumn
//            }
            else -> 0
        }
    }
}
