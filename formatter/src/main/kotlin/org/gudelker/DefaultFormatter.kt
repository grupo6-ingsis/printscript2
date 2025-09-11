package org.gudelker
import org.gudelker.analyzer.Analyzer
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class DefaultFormatter(
    private val analyzers: List<Analyzer>,
) : Formatter {
    override fun format(
        statement: Statement,
        rules: Map<String, FormatterRule>,
    ): String {
        return formatNode(statement, rules)
    }

    fun formatNode(
        node: Statement,
        rules: Map<String, FormatterRule>,
    ): String {
        val analyzer =
            analyzers.firstOrNull { it.canHandle(node) }
                ?: throw IllegalArgumentException("No analyzer found for ${node::class.simpleName}")
        println("hola")
        return analyzer.format(node, rules, this)
    }
}
