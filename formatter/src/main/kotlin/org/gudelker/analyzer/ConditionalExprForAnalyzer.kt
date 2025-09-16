package org.gudelker.analyzer

import org.gudelker.expressions.ConditionalExpression
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.rulevalidator.RuleValidatorFormatter
import org.gudelker.statements.interfaces.Statement

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
        val ifKeyword = statement.ifKeyword.value
        val condition = statement.condition

        val formattedCondition = formatter.format(condition, formatterRuleMap)

        var resultString = "$ifKeyword ($formattedCondition)"

        val openBrace = statement.ifOpenBracket
        val openBraceColumnDiff = openBrace!!.position.startLine - statement.ifKeyword.position.startLine
        val newLines = "\n".repeat(openBraceColumnDiff)
        if (openBraceColumnDiff > 0) {
            val spacesBeforeOpenBrace = " ".repeat(openBrace.position.startColumn - 1)
            resultString += newLines
            resultString += spacesBeforeOpenBrace
            resultString += openBrace.value
        } else {
            val spacesBeforeOpenBrace =
                " ".repeat(
                    (openBrace.position.startColumn - statement.closeParenthesis.position.endColumn) - 1,
                )
            resultString += spacesBeforeOpenBrace
            resultString += openBrace.value
        }

        val ifBody = statement.ifBody
        val elseBody = statement.elseBody

        val formattedIfBody = ifBody.joinToString("\n") { formatter.format(it, formatterRuleMap).trimEnd() }

        val ifColumn = statement.ifKeyword.position.startColumn - 1
        val indentClosingBrace = " ".repeat(ifColumn)

        resultString += "\n$formattedIfBody\n$indentClosingBrace}"

        if (elseBody != null) {
            val formattedElseBody =
                statement.elseBody?.let {
                    it.joinToString("\n") { stmt -> formatter.format(stmt, formatterRuleMap).trimEnd() }
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
}
