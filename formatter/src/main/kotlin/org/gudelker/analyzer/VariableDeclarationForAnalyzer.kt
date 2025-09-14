package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.expressions.Binary
import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.expressions.Unary
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import kotlin.text.compareTo
import kotlin.text.equals

class VariableDeclarationForAnalyzer(private val rulesValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableDeclaration
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement is VariableDeclaration) {
            val keyword = statement.keywordCombo.value
            val identifier = statement.identifierCombo.value
            var resultString = "$keyword $identifier"
            if (statement.colon != null && statement.type != null) {
                val spacesBeforeColon =
                    if (statement.colon!!.position.startColumn
                        > statement.identifierCombo.position.endColumn
                    ) {
                        " "
                    } else {
                        ""
                    }
                val spacesAfterColon =
                    if (statement.type!!.position.startColumn
                        > statement.colon!!.position.endColumn
                    ) {
                        " "
                    } else {
                        ""
                    }

                resultString += "$spacesBeforeColon${statement.colon!!.value}$spacesAfterColon${statement.type!!.value}"
            }
            if (statement.equals != null && statement.value != null) {
                val previousEndColumn =
                    if (statement.type != null) {
                        statement.type!!.position.endColumn
                    } else {
                        statement.identifierCombo.position.endColumn
                    }

                val spacesBeforeEquals = if (statement.equals!!.position.startColumn > previousEndColumn) " " else ""
                val spacesAfterEquals = calculateSpacesAfterEquals(statement.value!!, statement.equals!!)

                val valueFormatted = formatter.formatNode(statement.value!!, formatterRuleMap)
                resultString += "$spacesBeforeEquals${statement.equals!!.value}$spacesAfterEquals$valueFormatted"
            }
            resultString += ";\n"
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
    ): String {
        return when (value) {
            is Binary -> {
                val valuePos = value.position
                if (valuePos != null && valuePos.startColumn > equals.position.endColumn) " " else ""
            }
            is Unary -> {
                val valuePos = value.position
                if (valuePos != null && valuePos.startColumn > equals.position.endColumn) " " else ""
            }
            else -> " "
        }
    }
}
