package org.gudelker.linterloader

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.gudelker.linter.LinterConfig
import java.io.File

class YamlLinterConfigLoaderToMap(private val path: String) : LinterConfigLoader {
    private val yamlMapper =
        ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule.Builder().build())
        }

    override fun loadConfig(): Map<String, LinterConfig> {
        val file = File(path)
        if (!file.exists()) {
            throw IllegalArgumentException("El archivo YAML no existe: $path")
        }

        return try {
            val typeRef = object : TypeReference<Map<String, Any>>() {}
            val yamlContent: Map<String, Any> = yamlMapper.readValue(file, typeRef)
            val rulesMap =
                yamlContent["rules"] as? Map<String, Any>
                    ?: throw IllegalArgumentException("No se encontró la sección 'rules' en el archivo YAML")

            val config =
                LinterConfig(
                    identifierFormat = rulesMap["identifierFormat"] as? String ?: "",
                    restrictPrintlnToIdentifierOrLiteral = rulesMap["restrictPrintlnToIdentifierOrLiteral"] as? Boolean ?: false,
                    restrictReadInputToIdentifierOrLiteral = rulesMap["restrictReadInputToIdentifierOrLiteral"] as? Boolean ?: false,
                )

            rulesMap.keys.associateWith { config }
        } catch (e: Exception) {
            throw RuntimeException("Error al leer el archivo YAML: ${e.message}", e)
        }
    }
}
