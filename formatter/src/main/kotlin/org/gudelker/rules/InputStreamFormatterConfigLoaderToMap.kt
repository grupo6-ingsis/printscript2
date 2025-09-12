package org.gudelker.rules

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream

class InputStreamFormatterConfigLoaderToMap(private val inputStream: InputStream) : FormatterConfigLoader {
    override fun loadConfig(): Map<String, FormatterRule> {
        val gson = Gson()
        val json = inputStream.bufferedReader().readText()
        val type = object : TypeToken<Map<String, FormatterRule>>() {}.type
        return gson.fromJson(json, type)
    }
}
