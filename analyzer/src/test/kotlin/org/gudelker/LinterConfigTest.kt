package org.gudelker

import kotlinx.serialization.json.Json
import org.gudelker.linter.DefaultLinterFactory
import org.gudelker.linter.Linter
import org.gudelker.linter.LinterConfig
import org.gudelker.utilities.Version
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class LinterConfigTest {
    @Test
    fun `should create and serialize LinterConfig correctly`() {
        val config =
            LinterConfig(
                identifierFormat = "snake_case",
                restrictPrintlnExpressions = false,
                restrictReadInputExpressions = true,
            )
        assertEquals("snake_case", config.identifierFormat)
        assertEquals(false, config.restrictPrintlnExpressions)

        val json = Json.encodeToString(LinterConfig.serializer(), config)
        val decoded = Json.decodeFromString(LinterConfig.serializer(), json)
        assertEquals(config, decoded)
    }

    @Test
    fun `should create linter V1 and V2`() {
        val linterV1: Linter =
            DefaultLinterFactory.createLinter(
                Version.V1,
            )
        val linterV2: Linter =
            DefaultLinterFactory.createLinter(Version.V2)
        assertNotNull(linterV1)
        assertNotNull(linterV2)
    }
}
