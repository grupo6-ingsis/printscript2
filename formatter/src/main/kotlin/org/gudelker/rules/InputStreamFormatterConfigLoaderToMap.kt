package org.gudelker.rules

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class InputStreamFormatterConfigLoaderToMap(
    private val inputStream: InputStream,
) : FormatterConfigLoader {
    private val defaultRules =
        mapOf(
            "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = false, quantity = 1),
            "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = false, quantity = 1),
            "enforce-spacing-around-equals" to FormatterRule(on = false, quantity = 1),
            "line-breaks-after-println" to FormatterRule(on = false, quantity = 1),
            "indent-inside-if" to FormatterRule(on = true, quantity = 2),
            "mandatory-single-space-separation" to FormatterRule(on = false, quantity = 1),
            "mandatory-space-surrounding-operations" to FormatterRule(on = false, quantity = 1),
            "mandatory-line-break-after-statement" to FormatterRule(on = false, quantity = 1),
            "if-brace-same-line" to FormatterRule(on = false, quantity = 1),
            "if-brace-below-line" to FormatterRule(on = false, quantity = 1),
        )

    override fun loadConfig(): Map<String, FormatterRule> {
        val gson =
            GsonBuilder()
                .registerTypeAdapter(FormatterRule::class.java, FormatterRuleDeserializer())
                .create()

        val json = inputStream.bufferedReader().readText()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val inputRules: Map<String, Any> =
            try {
                gson.fromJson(json, type)
            } catch (e: Exception) {
                return defaultRules
            }

        val resultRules =
            defaultRules.keys.associateWith {
                FormatterRule(on = false, quantity = defaultRules[it]?.quantity ?: 0)
            }.toMutableMap()

        resultRules["mandatory-line-break-after-statement"] =
            FormatterRule(on = true, quantity = defaultRules["mandatory-line-break-after-statement"]?.quantity ?: 1)

        if (inputRules.containsKey("enforce-no-spacing-around-equals")) {
            val rule = gson.fromJson(gson.toJson(inputRules["enforce-no-spacing-around-equals"]), FormatterRule::class.java)
            if (rule.on) {
                resultRules["enforce-no-spacing-around-equals"] = FormatterRule(on = true, quantity = 0)
                resultRules["enforce-spacing-around-equals"] = FormatterRule(on = false, quantity = 1)
            }
        }

        // Process all input rules
        for (entry in inputRules.entries) {
            val key = entry.key
            if (key != "enforce-no-spacing-around-equals") {
                val ruleValue = entry.value
                val rule =
                    when (ruleValue) {
                        is Boolean -> FormatterRule(on = ruleValue, quantity = defaultRules[key]?.quantity ?: 1)
                        is Number -> FormatterRule(on = true, quantity = ruleValue.toInt())
                        else -> gson.fromJson(gson.toJson(ruleValue), FormatterRule::class.java)
                    }
                resultRules[key] = rule
            }
        }

        return resultRules
    }
}
