package org.gudelker.linterloader

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.gudelker.linter.LinterConfig
import java.io.InputStream
import java.io.InputStreamReader

class InputStreamLinterConfigLoaderToMap(private val inputStream: InputStream) : LinterConfigLoader {
    override fun loadConfig(): Map<String, LinterConfig> {
        val gson = Gson()
        val jsonObject = gson.fromJson(InputStreamReader(inputStream), JsonObject::class.java)

        // Default config
        var identifierFormat = ""
        var restrictPrintln = false
        var restrictReadInput = false

        // Process the JSON at any level (direct or inside "rules")
        val configObject = if (jsonObject.has("rules")) jsonObject.getAsJsonObject("rules") else jsonObject

        // Check all properties with normalization
        configObject.entrySet().forEach { (key, element) ->
            when (normalizeKey(key)) {
                "identifierformat" ->
                    identifierFormat = getStringValue(element)
                "restrictprintlnexpressions", "mandatoryvariableorliteralinprintln" ->
                    restrictPrintln = getBoolValue(element)
                "restrictreadinputexpressions", "mandatoryvariableorliteralinreadinput" ->
                    restrictReadInput = getBoolValue(element)
            }
        }
        val config =
            LinterConfig(
                identifierFormat = identifierFormat,
                restrictPrintlnExpressions = restrictPrintln,
                restrictReadInputExpressions = restrictReadInput,
            )

        return mapOf(
            "identifierFormat" to config,
            "restrictPrintlnExpressions" to config,
            "restrictReadInputExpressions" to config,
        )
    }

    // Other methods remain the same
    private fun normalizeKey(key: String): String {
        return key.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
    }

    private fun getStringValue(element: JsonElement): String {
        return if (element.isJsonPrimitive) element.asString else ""
    }

    private fun getBoolValue(element: JsonElement): Boolean {
        if (element.isJsonPrimitive) {
            val primitive = element.asJsonPrimitive
            if (primitive.isBoolean) {
                return primitive.asBoolean
            } else if (primitive.isString) {
                val str = primitive.asString.lowercase()
                return str == "true" || str == "yes" || str == "1" || str == "on"
            }
        }
        return false
    }
}
