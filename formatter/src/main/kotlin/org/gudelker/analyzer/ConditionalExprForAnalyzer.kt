package org.gudelker.analyzer

import org.gudelker.expressions.ConditionalExpression
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.utils.FormatterUtils

class ConditionalExprForAnalyzer(
    private val ruleValidators: List<RuleValidatorFormatter>,
) : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is ConditionalExpression
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is ConditionalExpression) {
            return ""
        }
        val ifKeyword = getIfKeyword(statement)

        val condition = getCondition(statement)
        val formattedCondition = formatter.format(condition, formatterRuleMap)

        var resultString = "$ifKeyword ($formattedCondition)"

        val openBrace = getOpenBrace(statement)
        val openBraceColumnDiff = calculateOpenBraceColumnDiff(openBrace, statement)

        val newLines = FormatterUtils.generateNewLines(openBraceColumnDiff)

        if (openBrace == null) {
            return resultString
        }

        val openBraceColumn = getOpenBraceColumn(openBrace)
        val closeParenColumn = getCloseParenColumn(statement)

        resultString =
            formatOpenBrace(
                openBraceColumnDiff,
                openBraceColumn,
                resultString,
                newLines,
                openBrace,
                closeParenColumn,
            )

        val ifBody = statement.ifBody
        val elseBody = statement.elseBody

        val formattedIfBody = getFormattedIfBody(ifBody, formatter, formatterRuleMap)

        val ifColumn = getIfColumn(statement)
        val indentClosingBrace = FormatterUtils.generateSpaces(ifColumn)

        resultString += "\n$formattedIfBody\n$indentClosingBrace}"

        if (elseBody != null) {
            val formattedElseBody =
                statement.elseBody?.let {
                    getFormattedIfBody(it, formatter, formatterRuleMap)
                }
            resultString += formattedElseBody
        }

        ruleValidators.forEach { validator ->
            if (validator.matches(formatterRuleMap)) {
                resultString = validator.applyRule(resultString, statement, formatterRuleMap)
            }
        }

        return resultString
    }

    private fun getIfColumn(statement: ConditionalExpression) = statement.ifKeyword.position.startColumn - 1

    private fun getFormattedIfBody(
        ifBody: List<Statement>,
        formatter: DefaultFormatter,
        formatterRuleMap: Map<String, FormatterRule>,
    ) = ifBody.joinToString("\n") { formatter.format(it, formatterRuleMap).trimEnd() }

    private fun getCloseParenColumn(statement: ConditionalExpression) = statement.closeParenthesis.position.endColumn

    private fun getOpenBraceColumn(openBrace: ComboValuePosition<String>) = openBrace.position.startColumn

    private fun getOpenBrace(statement: ConditionalExpression) = statement.ifOpenBracket

    private fun getCondition(statement: ConditionalExpression) = statement.condition

    private fun getIfKeyword(statement: ConditionalExpression) = statement.ifKeyword.value

    private fun formatOpenBrace(
        openBraceColumnDiff: Int,
        openBraceColumn: Int,
        resultString: String,
        newLines: String,
        openBrace: ComboValuePosition<String>,
        closeParenColumn: Int,
    ): String {
        var resultString1 = resultString
        if (openBraceColumnDiff > 0) {
            val spacesBeforeOpenBrace = FormatterUtils.generateSpaces(openBraceColumn - 1)
            resultString1 += newLines
            resultString1 += spacesBeforeOpenBrace
            resultString1 += openBrace.value
        } else {
            val spacesBeforeOpenBrace = FormatterUtils.generateSpaces((openBraceColumn - closeParenColumn) - 1)
            resultString1 += spacesBeforeOpenBrace
            resultString1 += openBrace.value
        }
        return resultString1
    }

    private fun calculateOpenBraceColumnDiff(
        openBrace: ComboValuePosition<String>?,
        statement: ConditionalExpression,
    ) = openBrace!!.position.startLine - statement.ifKeyword.position.startLine
}
