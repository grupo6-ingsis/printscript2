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
import org.gudelker.statements.VariableReassignment
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.utils.FormatterUtils

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
        if (statement !is VariableReassignment) {
            return ""
        }

        val identifier = getIdentifier(statement)

        val numberOfSpacesBeforeEquals = getEqualsColumn(statement) - getIdentifierColumn(statement)

        val spacesBeforeEquals = FormatterUtils.generateSpaces(numberOfSpacesBeforeEquals - 1)
        val numberOfSpacesAfterEquals = calculateSpacesAfterEquals(statement.value, statement.equals)
        val spacesAfterEquals = FormatterUtils.generateSpaces(numberOfSpacesAfterEquals - 1)

        val value = formatter.format(statement.value, formatterRuleMap)
        var result = "$identifier$spacesBeforeEquals=$spacesAfterEquals$value;"

        result = lookForAndApplyOtherRules(formatterRuleMap, result, statement)

        return result
    }

    private fun lookForAndApplyOtherRules(
        formatterRuleMap: Map<String, FormatterRule>,
        result: String,
        statement: Statement,
    ): String {
        var result1 = result
        ruleValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                result1 = validator.applyRule(result1, statement, formatterRuleMap)
            }
        }
        return result1
    }

    private fun getIdentifierColumn(statement: VariableReassignment) = statement.identifier.position.endColumn

    private fun getEqualsColumn(statement: VariableReassignment) = statement.equals.position.startColumn

    private fun getIdentifier(statement: VariableReassignment) = statement.identifier

    private fun calculateSpacesAfterEquals(
        value: CanBeCallStatement,
        equals: ComboValuePosition<String>,
    ): Int {
        val equalsEnd = equals.position.endColumn
        val equalsStart = equals.position.startColumn

        return when (value) {
            is LiteralBoolean -> value.value.position.startColumn - equalsStart
            is LiteralNumber,
            is LiteralString,
            is LiteralIdentifier,
            is InvocableExpression,
            -> {
                val pos =
                    when (value) {
                        is LiteralNumber -> value.value.position
                        is LiteralString -> value.value.position
                        is LiteralIdentifier -> value.value.position
                        is InvocableExpression -> value.functionName.position
                        else -> null
                    }
                pos?.startColumn?.minus(equalsEnd) ?: 0
            }
            is Binary -> value.position?.startColumn?.minus(equalsEnd) ?: 0
            is Unary -> value.operator.position.startColumn - equalsStart
            else -> 0
        }
    }
}
