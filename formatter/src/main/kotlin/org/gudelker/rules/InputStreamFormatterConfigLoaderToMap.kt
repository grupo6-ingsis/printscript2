package org.gudelker.rules

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class InputStreamFormatterConfigLoaderToMap(
    private val inputStream: InputStream,
) : FormatterConfigLoader {
    private val allRuleNames =
        listOf(
            "enforce-spacing-before-colon-in-declaration",
            "enforce-spacing-after-colon-in-declaration",
            "enforce-spacing-around-equals",
            "indent-inside-if",
            "line-breaks-after-println",
        )

    override fun loadConfig(): Map<String, FormatterRule> {
        val gson =
            GsonBuilder()
                .registerTypeAdapter(FormatterRule::class.java, FormatterRuleDeserializer())
                .create()

        val json = inputStream.bufferedReader().readText()
        val type = object : TypeToken<Map<String, FormatterRule>>() {}.type
        val map: MutableMap<String, FormatterRule> = gson.fromJson(json, type)

        allRuleNames.forEach { ruleName ->
            if (!map.containsKey(ruleName)) {
                map[ruleName] = FormatterRule(on = true, quantity = 1)
            }
        }

        return map
    }
}
