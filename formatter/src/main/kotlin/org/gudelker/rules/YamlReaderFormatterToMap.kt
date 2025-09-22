package org.gudelker.rules

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

class YamlReaderFormatterToMap(private val filePath: String) : FormatterConfigLoader {
    private val yamlMapper =
        ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule.Builder().build())
        }

    override fun loadConfig(): Map<String, FormatterRule> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("El archivo YAML no existe: $filePath")
        }

        return try {
            val yamlContent: Map<String, Map<String, Any>> = yamlMapper.readValue(file)
            yamlContent.mapValues { (_, ruleData) ->
                FormatterRule(
                    on = ruleData["on"] as? Boolean ?: false,
                    quantity = ruleData["quantity"] as? Int ?: 0,
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("Error al leer el archivo YAML: ${e.message}", e)
        }
    }
}
