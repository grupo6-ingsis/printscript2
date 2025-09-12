package org.gudelker.rules

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class InputStreamFormatterConfigLoaderToMap(private val inputStream: InputStream) : FormatterConfigLoader {
    override fun loadConfig(): Map<String, FormatterRule> {
        val gson = Gson()
        val json = inputStream.bufferedReader().readText()

        // Primero lo leo como Map<String, Int>
        val type = object : TypeToken<Map<String, Int>>() {}.type
        val raw: Map<String, Int> = gson.fromJson(json, type)

        // Después lo transformo a Map<String, FormatterRule>
        return raw.mapValues { (_, quantity) ->
            FormatterRule(on = true, quantity = quantity)
        }
    }
}
