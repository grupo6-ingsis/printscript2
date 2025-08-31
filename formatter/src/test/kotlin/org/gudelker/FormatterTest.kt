import org.gudelker.DefaultFormatter
import org.gudelker.LiteralNumber
import org.gudelker.LiteralString
import org.gudelker.Statement
import org.gudelker.VariableDeclaration
import org.gudelker.analyzer.BinaryAnalyzer
import org.gudelker.analyzer.CallableAnalyzer
import org.gudelker.analyzer.GroupingAnalyzer
import org.gudelker.analyzer.LiteralIdentifierAnalyzer
import org.gudelker.analyzer.LiteralNumberAnalyzer
import org.gudelker.analyzer.LiteralStringAnalyzer
import org.gudelker.analyzer.UnaryAnalyzer
import org.gudelker.analyzer.VariableDeclarationAnalyzer
import org.gudelker.rules.JsonReaderToMap
import org.gudelker.rules.Rule
import kotlin.test.Test

class FormatterTest {
    private fun formatStatementList(
        statements: List<Statement>,
        rules: Map<String, Rule>,
    ): String {
        val formatter =
            DefaultFormatter(
                listOf(
                    VariableDeclarationAnalyzer(),
                    LiteralNumberAnalyzer(),
                    LiteralIdentifierAnalyzer(),
                    LiteralStringAnalyzer(),
                    GroupingAnalyzer(),
                    UnaryAnalyzer(),
                    CallableAnalyzer(),
                    BinaryAnalyzer(),
                ),
            )
        return statements.joinToString("\n") { formatter.format(it, rules) }
    }

    @Test
    fun testFormat() {
        val statementsList =
            listOf(
                VariableDeclaration("x", "Int", LiteralNumber(5)),
                VariableDeclaration("y", "String", LiteralString("hola")),
            )

        val reader =
            JsonReaderToMap("C:/Users/agusg/faculty/ingSis/printscript2/formatter/src/main/kotlin/org/gudelker/rules/formatterconfig.json")
        val ruleMap = reader.jsonToMap()

        val result = formatStatementList(statementsList, ruleMap)
        println(result)
    }
}
