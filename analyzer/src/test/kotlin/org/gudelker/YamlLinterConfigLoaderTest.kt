import org.gudelker.linterloader.YamlLinterConfigLoaderToMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class YamlLinterConfigLoaderTest {
    @Test
    fun `loads config from existing linterconfig yaml`() {
        val loader =
            YamlLinterConfigLoaderToMap(
                "/Users/pedrodelaguila/faculty/ingsis/printscript2/analyzer/src/main/kotlin/org/gudelker/linterconfig.yaml",
            )
        val configMap = loader.loadConfig()

        assertEquals(3, configMap.size)
        assertEquals("camelCase", configMap["identifierFormat"]?.identifierFormat)
        assertEquals(true, configMap["restrictPrintlnToIdentifierOrLiteral"]?.restrictPrintlnToIdentifierOrLiteral)
        assertEquals(true, configMap["restrictReadInputToIdentifierOrLiteral"]?.restrictReadInputToIdentifierOrLiteral)
    }
}
