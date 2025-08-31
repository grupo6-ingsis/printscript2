import org.gudelker.DefaultFormatter
import org.gudelker.LiteralNumber
import org.gudelker.VariableDeclaration
import org.gudelker.analyzer.LiteralNumberAnalyzer
import org.gudelker.analyzer.VariableDeclarationAnalyzer
import org.gudelker.rules.JsonReaderToMap
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {
    @Test
    fun testFormat() {
        val statement = VariableDeclaration("x", "Int", LiteralNumber(5))
        val formatter = DefaultFormatter(listOf(VariableDeclarationAnalyzer(), LiteralNumberAnalyzer()))
        val reader =
            JsonReaderToMap("C:/Users/agusg/faculty/ingSis/printscript2/formatter/src/main/kotlin/org/gudelker/rules/formatterconfig.json")
        val ruleMap = reader.jsonToMap()
        val result = formatter.format(statement, ruleMap)
        assertEquals("let x : Int = 5;", result)
        println(result)
    }
}
