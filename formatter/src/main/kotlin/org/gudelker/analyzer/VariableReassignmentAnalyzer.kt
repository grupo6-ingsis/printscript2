package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.VariableReassignment
import org.gudelker.rules.Rule
import org.gudelker.utils.FormatterUtils

class VariableReassignmentAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableReassignment
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        val reassignment = statement as VariableReassignment
        val identifier = reassignment.identifier
        val value = formatter.format(reassignment.value, ruleMap)
        val spacesBefore = FormatterUtils.getDeclarationSpaces("beforeDeclaration", ruleMap)
        val spacesAfter = FormatterUtils.getDeclarationSpaces("afterDeclaration", ruleMap)

        return "$identifier$spacesBefore=$spacesAfter$value;"
    }
}
