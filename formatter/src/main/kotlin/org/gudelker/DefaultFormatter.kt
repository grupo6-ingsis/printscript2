package org.gudelker
import org.gudelker.analyzer.Analyzer
import org.gudelker.evaluator.RuleEvaluator
import kotlin.reflect.full.memberProperties

class DefaultFormatter(
    private val loader: FormatterConfigLoader,
    private val analyzers: List<Analyzer>,
) : Formatter {
    override fun format(statements: List<Statement>): String {
        val result: StringBuilder = StringBuilder()
        val rules: FormatterConfig = loader.loadConfig()

        val evaluators: List<RuleEvaluator> =
            FormatterConfig::class.memberProperties
                .flatMap { prop ->
                    val name = prop.name
                    val value = prop.get(rules)
                    analyzers.mapNotNull { analyzer ->
                        analyzer.isRuleOn(name, value)
                    }
                }

        for (statement in statements) {
            for (evaluator in evaluators) {
                val formattedRule = evaluator.evaluateRule(statement)
                result.append(formattedRule)
            }
        }
        return result.toString()
    }
}
