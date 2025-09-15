package org.gudelker.rules

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class InputStreamFormatterConfigLoaderToMap(
    private val inputStream: InputStream,
) : FormatterConfigLoader {
    private val defaultRules =
        mapOf(
            "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = true, quantity = 1),
            "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = true, quantity = 0),
            "enforce-spacing-around-equals" to FormatterRule(on = true, quantity = 1),
            "line-breaks-after-println" to FormatterRule(on = true, quantity = 1),
            "indent-inside-if" to FormatterRule(on = true, quantity = 4),
            "mandatory-single-space-separation" to FormatterRule(on = true, quantity = 1),
            "mandatory-space-surrounding-operations" to FormatterRule(on = true, quantity = 1),
            "mandatory-line-break-after-statement" to FormatterRule(on = true, quantity = 1),
            "if-brace-same-line" to FormatterRule(on = true, quantity = 1),
            "if-brace-below-line" to FormatterRule(on = false, quantity = 1),
        )

    private val externalToInternal =
        mapOf(
            "enforce-no-spacing-around-equals" to "enforce-spacing-around-equals",
            "enforce-spacing-around-equals" to "enforce-spacing-around-equals",
            "enforce-spacing-after-colon-in-declaration" to "enforce-spacing-after-colon-in-declaration",
            "enforce-spacing-before-colon-in-declaration" to "enforce-spacing-before-colon-in-declaration",
            "line-breaks-after-println" to "line-breaks-after-println",
            "indent-inside-if" to "indent-inside-if",
            "mandatory-single-space-separation" to "mandatory-single-space-separation",
            "mandatory-space-surrounding-operations" to "mandatory-space-surrounding-operations",
            "mandatory-line-break-after-statement" to "mandatory-line-break-after-statement",
            "if-brace-below-line" to "if-brace-below-line",
            "if-brace-same-line" to "if-brace-same-line",
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

        externalMap.forEach { (key, rule) ->
            val internalKey = externalToInternal[key] ?: return@forEach

            if (key == "enforce-no-spacing-around-equals") {
                finalMap[internalKey] = FormatterRule(on = !rule.on, quantity = 0)
            } else if (key == "if-brace-below-line" && rule.on) {
                finalMap["if-brace-below-line"] = FormatterRule(on = true, quantity = rule.quantity)
                finalMap["if-brace-same-line"] = FormatterRule(on = false, quantity = rule.quantity)
            } else if (key == "if-brace-same-line" && rule.on) {
                finalMap["if-brace-same-line"] = FormatterRule(on = true, quantity = rule.quantity)
                finalMap["if-brace-below-line"] = FormatterRule(on = false, quantity = rule.quantity)
            } else {
                finalMap[internalKey] = FormatterRule(on = rule.on, quantity = rule.quantity)
            }
        }

        return finalMap
    }
}
