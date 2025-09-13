package org.gudelker.rules

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class InputStreamFormatterConfigLoaderToMap(
    private val inputStream: InputStream,
) : FormatterConfigLoader {
    val defaultRules =
        mapOf(
            "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
            "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
            "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            "line-breaks-after-println" to FormatterRule(on = true, quantity = 1),
            "indent-inside-if" to FormatterRule(on = true, quantity = 1),
        )
    val externalToInternal =
        mapOf(
            "enforce-no-spacing-around-equals" to "enforce-spacing-around-equals",
            "enforce-spacing-around-equals" to "enforce-spacing-around-equals",
            "enforce-spacing-after-colon-in-declaration" to "enforce-spacing-after-colon-in-declaration",
            "enforce-spacing-before-colon-in-declaration" to "enforce-spacing-before-colon-in-declaration",
            "line-breaks-after-println" to "line-breaks-after-println",
            "indent-inside-if" to "indent-inside-if",
        )

    override fun loadConfig(): Map<String, FormatterRule> {
        val gson =
            GsonBuilder()
                .registerTypeAdapter(FormatterRule::class.java, FormatterRuleDeserializer())
                .create()

        val json = inputStream.bufferedReader().readText()
        val type = object : TypeToken<Map<String, FormatterRule>>() {}.type
        val externalMap: Map<String, FormatterRule> = gson.fromJson(json, type)

        val finalMap = defaultRules.toMutableMap()

        // Reemplazar valores por los que vienen del JSON externo si existen
        externalMap.forEach { (key, rule) ->
            val internalKey = externalToInternal[key] ?: return@forEach
            // Si es "no-spacing" invertimos on y usamos quantity 1
            if (key == "enforce-no-spacing-around-equals") {
                finalMap[internalKey] = FormatterRule(on = !rule.on, quantity = 1)
            } else {
                finalMap[internalKey] = FormatterRule(on = rule.on, quantity = rule.quantity)
            }
        }

        return finalMap
    }
}
