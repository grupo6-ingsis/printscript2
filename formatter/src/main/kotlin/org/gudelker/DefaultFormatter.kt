package org.gudelker
import org.gudelker.analyzer.Analyzer
import org.gudelker.rules.Rule

class DefaultFormatter(
    private val analyzers: List<Analyzer>,
) : Formatter {
    override fun format(
        statement: Statement,
        rules: Map<String, Rule>,
    ): String {
        return formatNode(statement, rules)
    }

    fun formatNode(
        node: Statement,
        rules: Map<String, Rule>,
    ): String {
        val analyzer =
            analyzers.firstOrNull { it.canHandle(node) }
                ?: throw IllegalArgumentException("No analyzer found for ${node::class.simpleName}")

        return analyzer.format(node, rules, this)
    }
}
