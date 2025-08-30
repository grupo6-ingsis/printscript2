package org.gudelker

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File

class JsonConfigLoader(override val path: String) : FormatterConfigLoader {
    override fun loadConfig(): FormatterConfig {
        val jsonString = File(path).readText()
        val rulesJson = Json.parseToJsonElement(jsonString).jsonObject["rules"]?.toString() ?: error("No rules found")
        return Json.decodeFromString<FormatterConfig>(rulesJson)
    }
}
