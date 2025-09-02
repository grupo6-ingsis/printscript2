package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.VariableDeclaration
import org.gudelker.rules.Rule
import org.gudelker.utils.FormatterUtils

class VariableDeclarationAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is VariableDeclaration
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        val declaration = statement as VariableDeclaration

        val keyword = statement.keyword

        val spacesBefore = FormatterUtils.getDeclarationSpaces("beforeDeclaration", ruleMap)
        val spacesAfter = FormatterUtils.getDeclarationSpaces("afterDeclaration", ruleMap)

        val typeStr = declaration.type?.let { "$spacesBefore:${spacesAfter}$it" } ?: ""

        val assignSpaces = FormatterUtils.getAssignationSpaces("assignDeclaration", ruleMap)

        val valueFormatted = formatter.formatNode(declaration.value, ruleMap)

        return "$keyword ${declaration.identifier}${typeStr}$assignSpaces=${assignSpaces}$valueFormatted;"
    }
}
