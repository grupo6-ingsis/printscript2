package org.gudelker

import org.gudelker.rules.YamlReaderFormatterToMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ConfigLoaderYamlToMapTest {
    @Test
    fun `debería cargar correctamente la configuración del formatter desde un archivo YAML`(
        @TempDir tempDir: Path,
    ) {
        val configFile = File(tempDir.toFile(), "formatter.yaml")
        configFile.writeText(
            """
            enforce-spacing-after-colon-in-declaration:
              on: false
              quantity: 1
            enforce-spacing-before-colon-in-declaration:
              on: false
              quantity: 1
            """.trimIndent(),
        )

        val configLoader = YamlReaderFormatterToMap(configFile.absolutePath)
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
    fun `debería cargar correctamente todas las reglas del archivo YAML completo`(
        @TempDir tempDir: Path,
    ) {
        val configFile = File(tempDir.toFile(), "formatterconfig.yaml")
        configFile.writeText(
            """
            enforce-spacing-after-colon-in-declaration:
              on: true
              quantity: 1
            enforce-spacing-before-colon-in-declaration:
              on: true
              quantity: 1
            enforce-spacing-around-equals:
              on: true
              quantity: 0
            line-breaks-after-println:
              on: false
              quantity: 1
            indent-inside-if:
              on: true
              quantity: 1
            if-brace-same-line:
              on: true
            """.trimIndent(),
        )

        val configLoader = YamlReaderFormatterToMap(configFile.absolutePath)
        val configs = configLoader.loadConfig()

        assertNotNull(configs)
        assertEquals(6, configs.size)

        val spacingAfterColon = configs["enforce-spacing-after-colon-in-declaration"]
        assertNotNull(spacingAfterColon)
        assertEquals(true, spacingAfterColon?.on)
        assertEquals(1, spacingAfterColon?.quantity)

        val spacingAroundEquals = configs["enforce-spacing-around-equals"]
        assertNotNull(spacingAroundEquals)
        assertEquals(true, spacingAroundEquals?.on)
        assertEquals(0, spacingAroundEquals?.quantity)

        val ifBraceSameLine = configs["if-brace-same-line"]
        assertNotNull(ifBraceSameLine)
        assertEquals(true, ifBraceSameLine?.on)
        assertEquals(0, ifBraceSameLine?.quantity)
    }
}
