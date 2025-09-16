package org.gudelker

import org.gudelker.rules.JsonReaderFormatterToMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ConfigLoaderMapTest {
    @Test
    fun `debería cargar correctamente la configuración del formatter desde un archivo JSON`(
        @TempDir tempDir: Path,
    ) {
        // Crear un archivo de configuración temporal
        val configFile = File(tempDir.toFile(), "formatter.json")
        configFile.writeText(
            """
            {
              "enforce-spacing-after-colon-in-declaration": {
                "on": false,
                "quantity": 1
              },
              "enforce-spacing-before-colon-in-declaration": {
                "on": false,
                "quantity": 1
              }
            }
            """.trimIndent(),
        )

        val configLoader = JsonReaderFormatterToMap(configFile.absolutePath)
        val configs = configLoader.loadConfig()

        assertNotNull(configs)
        assertEquals(2, configs.size)

        val afterColonRule = configs["enforce-spacing-after-colon-in-declaration"]
        assertNotNull(afterColonRule)
        assertEquals(false, afterColonRule?.on)
        assertEquals(1, afterColonRule?.quantity)

        val beforeColonRule = configs["enforce-spacing-before-colon-in-declaration"]
        assertNotNull(beforeColonRule)
        assertEquals(false, beforeColonRule?.on)
        assertEquals(1, beforeColonRule?.quantity)
    }

    @Test
    fun `debería cargar correctamente todas las reglas del archivo formatterconfig`(
        @TempDir tempDir: Path,
    ) {
        // Copiar el contenido del archivo formatterconfig.json a un archivo temporal
        val configFile = File(tempDir.toFile(), "formatterconfig.json")
        configFile.writeText(
            """
            {
              "enforce-spacing-after-colon-in-declaration": {
                "on": false,
                "quantity": 1
              },
              "enforce-spacing-before-colon-in-declaration": {
                "on": false,
                "quantity": 1
              },
              "enforce-spacing-around-equals": {
                "on": false,
                "quantity": 1
              },
              "line-breaks-after-println": {
                "on": false,
                "quantity": 1
              },
              "indent-inside-if": {
                "on": false,
                "quantity": 1
              }
            }
            """.trimIndent(),
        )

        val configLoader = JsonReaderFormatterToMap(configFile.absolutePath)
        val configs = configLoader.loadConfig()

        assertNotNull(configs)
        assertEquals(5, configs.size)

        // Verificar cada regla
        val rules =
            listOf(
                "enforce-spacing-after-colon-in-declaration",
                "enforce-spacing-before-colon-in-declaration",
                "enforce-spacing-around-equals",
                "line-breaks-after-println",
                "indent-inside-if",
            )

        rules.forEach { ruleName ->
            val rule = configs[ruleName]
            assertNotNull(rule, "La regla $ruleName debería existir")
            assertEquals(false, rule?.on, "La regla $ruleName debería tener 'on' en false")
            assertEquals(1, rule?.quantity, "La regla $ruleName debería tener 'quantity' en 1")
        }
    }

    @Test
    fun `debería manejar reglas con diferentes valores`(
        @TempDir tempDir: Path,
    ) {
        val configFile = File(tempDir.toFile(), "custom-formatter.json")
        configFile.writeText(
            """
            {
              "enforce-spacing-after-colon-in-declaration": {
                "on": true,
                "quantity": 2
              },
              "line-breaks-after-println": {
                "on": true,
                "quantity": 3
              }
            }
            """.trimIndent(),
        )

        val configLoader = JsonReaderFormatterToMap(configFile.absolutePath)
        val configs = configLoader.loadConfig()

        assertNotNull(configs)
        assertEquals(2, configs.size)

        val spacingRule = configs["enforce-spacing-after-colon-in-declaration"]
        assertNotNull(spacingRule)
        assertEquals(true, spacingRule?.on)
        assertEquals(2, spacingRule?.quantity)

        val lineBreakRule = configs["line-breaks-after-println"]
        assertNotNull(lineBreakRule)
        assertEquals(true, lineBreakRule?.on)
        assertEquals(3, lineBreakRule?.quantity)
    }
}
