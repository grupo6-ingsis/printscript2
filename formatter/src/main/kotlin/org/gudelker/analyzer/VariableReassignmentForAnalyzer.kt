package org.gudelker.analyzer

import org.gudelker.expressions.Binary
import org.gudelker.expressions.CallableCall
import org.gudelker.expressions.CanBeCallStatement
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
        val reassignment = statement as VariableReassignment
        val identifier = reassignment.identifier
        val numberOfSpacesBeforeEquals =
            reassignment.equals.position.startColumn - reassignment.identifier.position.endColumn
        val spacesBeforeEquals = " ".repeat(numberOfSpacesBeforeEquals - 1)
        val numberOfSpacesAfterEquals = calculateSpacesAfterEquals(reassignment.value, reassignment.equals)
        val spacesAfterEquals = " ".repeat(numberOfSpacesAfterEquals - 1)
        val value = formatter.format(reassignment.value, formatterRuleMap)
        var result = "$identifier$spacesBeforeEquals=$spacesAfterEquals$value;"
        ruleValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                result = validator.applyRule(result, statement, formatterRuleMap)
            }
        }

        return result
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
            is CallableCall -> {
                val valuePos = value.functionName.position
                valuePos.startColumn - equals.position.endColumn
            }

            else -> 0
        }
    }
}
