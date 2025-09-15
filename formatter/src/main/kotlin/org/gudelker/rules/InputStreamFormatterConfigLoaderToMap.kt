package org.gudelker.rules

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class InputStreamFormatterConfigLoaderToMap(
    private val inputStream: InputStream,
) : FormatterConfigLoader {
    private val gson = GsonBuilder().create()

    override fun loadConfig(): Map<String, FormatterRule> {
        val reader = inputStream.bufferedReader()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val rawMap: Map<String, Any> = gson.fromJson(reader, type)

        return rawMap.mapValues { (key, value) ->
            when (value) {
                is Boolean -> {
                    if (key.startsWith("enforce-no-spacing")) {
                        FormatterRule(on = value, quantity = 0)
                    } else {
                        FormatterRule(on = value, quantity = 1)
                    }
                }
                is Number -> FormatterRule(on = true, quantity = value.toInt())
                else -> throw IllegalArgumentException("Valor inesperado en config: $value")
            }
        }
    }
}
