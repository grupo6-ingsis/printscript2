import org.gudelker.DefaultFormatter
import org.gudelker.JsonConfigLoader
import org.gudelker.LiteralNumber
import org.gudelker.VariableDeclaration
import org.gudelker.analyzer.Analyzer
import org.gudelker.analyzer.SpaceBeforeColonAnalyzer
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatterTest {
    @Test
    fun testFormat() {
        val statement = VariableDeclaration("x", "Int", LiteralNumber(5))
        val analyzer: Analyzer = SpaceBeforeColonAnalyzer()

        val loader = JsonConfigLoader("src/main/kotlin/org/gudelker/formatterconfig.json")
        val formatter = DefaultFormatter(loader, listOf(analyzer))

        val result = formatter.format(listOf(statement))
        assertEquals("let x: Int = LiteralNumber(value=5)", result)
        println(result)
    }
}
