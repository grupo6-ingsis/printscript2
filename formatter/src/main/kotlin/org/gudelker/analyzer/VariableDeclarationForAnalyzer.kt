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
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.utils.FormatterUtils

class VariableDeclarationForAnalyzer(private val rulesValidators: List<RuleValidatorFormatter>) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableDeclaration
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is VariableDeclaration) {
            return ""
        }

        var resultString = formatUntilIdentifier(statement)

        if (statement.colon != null) {
            resultString = formatWithSpacesAroundColon(statement, resultString)
        }

        if (statement.equals != null && statement.type == null) {
            resultString = formatSpacesAroundEqualsWithoutType(statement, formatter, formatterRuleMap, resultString)
        }

        if (statement.equals != null && statement.type != null) {
            resultString = formatSpacesAroundEqualsWithType(statement, formatter, formatterRuleMap, resultString)
        }
        if (!resultString.contains(";")) {
            resultString += ";"
        }

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

    private fun formatSpacesAroundEqualsWithType(
        statement: VariableDeclaration,
        formatter: DefaultFormatter,
        formatterRuleMap: Map<String, FormatterRule>,
        resultString: String,
    ): String {
        var resultString1 = resultString
        val numberOfSpacesBeforeEquals = getEqualsColumn(statement) - getTypePosition(statement)
        val spacesBeforeEquals = FormatterUtils.generateSpaces(numberOfSpacesBeforeEquals - 1)
        val numberOfSpacesAfterEquals = calculateSpacesAfterEquals(statement.value!!, statement.equals!!)
        val spacesAfterEquals = FormatterUtils.generateSpaces(numberOfSpacesAfterEquals - 1)

        val valueFormatted = formatter.formatNode(statement.value!!, formatterRuleMap)
        resultString1 += "$spacesBeforeEquals${statement.equals!!.value}$spacesAfterEquals$valueFormatted"
        return resultString1
    }

    private fun getTypePosition(statement: VariableDeclaration) = statement.type!!.position.startColumn

    private fun formatSpacesAroundEqualsWithoutType(
        statement: VariableDeclaration,
        formatter: DefaultFormatter,
        formatterRuleMap: Map<String, FormatterRule>,
        resultString: String,
    ): String {
        var resultString1 = resultString
        val numberOfSpacesBeforeEquals = getEqualsColumn(statement) - getIdentifierColumn(statement)
        val spacesBeforeEquals = FormatterUtils.generateSpaces(numberOfSpacesBeforeEquals - 1)
        val numberOfSpacesAfterEquals = calculateSpacesAfterEquals(statement.value!!, statement.equals!!)
        val spacesAfterEquals = FormatterUtils.generateSpaces(numberOfSpacesAfterEquals - 1)

        val valueFormatted = formatter.formatNode(statement.value!!, formatterRuleMap)
        resultString1 += "$spacesBeforeEquals${statement.equals!!.value}$spacesAfterEquals$valueFormatted"
        return resultString1
    }

    private fun getEqualsColumn(statement: VariableDeclaration) = statement.equals!!.position.startColumn

    private fun formatWithSpacesAroundColon(
        statement: VariableDeclaration,
        resultString: String,
    ): String {
        var resultString1 = resultString

        val numberOfSpacesBeforeColon =
            getColonColumn(statement) - getIdentifierColumn(statement)
        val spacesBeforeColon = FormatterUtils.generateSpaces(numberOfSpacesBeforeColon - 1)
        val numberOfSpacesAfterColon = getTypeColumn(statement) - getColonColumn(statement)
        val spacesAfterColon = FormatterUtils.generateSpaces(numberOfSpacesAfterColon - 1)

        resultString1 += "$spacesBeforeColon${statement.colon!!.value}$spacesAfterColon${statement.type!!.value}"
        return resultString1
    }

    private fun getTypeColumn(statement: VariableDeclaration) = statement.type?.position!!.startColumn

    private fun getIdentifierColumn(statement: VariableDeclaration) = statement.identifierCombo.position.startColumn

    private fun getColonColumn(statement: VariableDeclaration) = statement.colon!!.position.startColumn

    private fun formatUntilIdentifier(statement: VariableDeclaration): String {
        val keyword = getKeyword(statement)
        val identifier = getIdentifier(statement)
        val keywordColumn = getKeywordColumn(statement)

        val spacesBeforeKeyword = FormatterUtils.generateSpaces(keywordColumn.startColumn - 1)
        var resultString = "$spacesBeforeKeyword$keyword $identifier"
        return resultString
    }

    private fun getKeywordColumn(statement: VariableDeclaration) = statement.keywordCombo.position

    private fun getIdentifier(statement: VariableDeclaration) = statement.identifierCombo.value

    private fun getKeyword(statement: VariableDeclaration) = statement.keywordCombo.value

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
                is Binary -> value.position?.startColumn ?: 0
                is Unary -> value.operator.position.startColumn
                is InvocableExpression -> value.functionName.position.startColumn
                else -> return 0
            }
        val equalsColumn =
            when (value) {
                is LiteralBoolean, is Unary -> equals.position.startColumn
                else -> equals.position.endColumn
            }
        return valueStartColumn - equalsColumn
    }
}
