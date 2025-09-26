package org.gudelker.analyzer

import org.gudelker.expressions.Binary
import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.expressions.InvocableExpression
import org.gudelker.expressions.LiteralBoolean
import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.expressions.LiteralNumber
import org.gudelker.expressions.LiteralString
import org.gudelker.expressions.Unary
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.utils.FormatterUtils

class ConstDeclarationForAnalyzer(private val rulesValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is ConstDeclaration
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is ConstDeclaration) {
            return ""
        }

        var resultString = formatUntilIdentifier(statement)

        if (statement.colon != null) {
            resultString = formatWithSpacesAroundColon(statement, resultString)
        }

        resultString = formatSpacesAroundEqualsAndValue(statement, formatter, formatterRuleMap, resultString)

        resultString += ";"
        resultString = lookForAndApplyOtherRules(formatterRuleMap, resultString, statement)

        return resultString
    }

    private fun lookForAndApplyOtherRules(
        formatterRuleMap: Map<String, FormatterRule>,
        resultString: String,
        statement: Statement,
    ): String {
        var resultString1 = resultString
        rulesValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                resultString1 = validator.applyRule(resultString1, statement, formatterRuleMap)
            }
        }
        return resultString1
    }

    private fun formatSpacesAroundEqualsAndValue(
        statement: ConstDeclaration,
        formatter: DefaultFormatter,
        formatterRuleMap: Map<String, FormatterRule>,
        resultString: String,
    ): String {
        var resultString1 = resultString
        val numberOfSpacesBeforeEquals = getEqualsColumn(statement) - getTypeStartColumn(statement)
        val spacesBeforeEquals = FormatterUtils.generateSpaces(numberOfSpacesBeforeEquals - 1)
        val numberOfSpacesAfterEquals = calculateSpacesAfterEquals(statement.value, statement.equals)
        val spacesAfterEquals = FormatterUtils.generateSpaces(numberOfSpacesAfterEquals - 1)

        val valueFormatted = formatter.formatNode(statement.value, formatterRuleMap)
        resultString1 += "$spacesBeforeEquals${getEquals(statement)}$spacesAfterEquals$valueFormatted"
        return resultString1
    }

    private fun getEquals(statement: ConstDeclaration) = statement.equals.value

    private fun formatUntilIdentifier(statement: ConstDeclaration): String {
        val keyword = getKeyword(statement)
        val identifier = getIdentifier(statement)
        val resultString = "$keyword $identifier"
        return resultString
    }

    private fun getTypeStartColumn(statement: ConstDeclaration) = statement.type!!.position.startColumn

    private fun getEqualsColumn(statement: ConstDeclaration) = statement.equals.position.startColumn

    private fun formatWithSpacesAroundColon(
        statement: ConstDeclaration,
        resultString: String,
    ): String {
        var resultString1 = resultString
        val numberOfSpacesBeforeColon = getColonColumn(statement) - getIdentifierColumn(statement)
        val spacesBeforeColon = FormatterUtils.generateSpaces(numberOfSpacesBeforeColon - 1)
        val numberOfSpacesAfterColon = getTypeColumn(statement) - getColonColumn(statement)
        val spacesAfterColon = FormatterUtils.generateSpaces(numberOfSpacesAfterColon - 1)

        resultString1 += "$spacesBeforeColon${statement.colon!!.value}$spacesAfterColon${statement.type!!.value}"
        return resultString1
    }

    private fun getTypeColumn(statement: ConstDeclaration) = statement.type?.position!!.startColumn

    private fun getIdentifierColumn(statement: ConstDeclaration) = statement.identifierCombo.position.startColumn

    private fun getColonColumn(statement: ConstDeclaration) = statement.colon!!.position.startColumn

    private fun getIdentifier(statement: ConstDeclaration) = statement.identifierCombo.value

    private fun getKeyword(statement: ConstDeclaration) = statement.keywordCombo.value

    private fun calculateSpacesAfterEquals(
        value: CanBeCallStatement,
        equals: ComboValuePosition<String>,
    ): Int {
        val valueStartColumn =
            when (value) {
                is LiteralBoolean -> value.value.position.startColumn
                is LiteralNumber -> value.value.position.startColumn
                is LiteralString -> value.value.position.startColumn
                is LiteralIdentifier -> value.value.position.startColumn
                is Binary -> value.position?.startColumn
                is Unary -> value.operator.position.startColumn
                is InvocableExpression -> value.functionName.position.startColumn
                else -> null
            } ?: return 0

        val equalsColumn =
            when (value) {
                is LiteralBoolean, is Unary -> equals.position.startColumn
                else -> equals.position.endColumn
            }

        return valueStartColumn - equalsColumn
    }
}
