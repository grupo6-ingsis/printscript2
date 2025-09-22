package org.gudelker

import org.gudelker.linterloader.JsonLinterConfigLoaderToMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonLinterConfigLoaderTest {
    @Test
    fun `loads config from existing linterconfig json`() {
        val loader =
            JsonLinterConfigLoaderToMap(
                "src/main/kotlin/org/gudelker/linterconfig.json",
            )
        val configMap = loader.loadConfig()

        assertEquals(3, configMap.size)
        assertEquals("camelCase", configMap["identifierFormat"]?.identifierFormat)
        assertEquals(true, configMap["restrictPrintlnToIdentifierOrLiteral"]?.restrictPrintlnToIdentifierOrLiteral)
        assertEquals(true, configMap["restrictReadInputToIdentifierOrLiteral"]?.restrictReadInputToIdentifierOrLiteral)
    }
}
