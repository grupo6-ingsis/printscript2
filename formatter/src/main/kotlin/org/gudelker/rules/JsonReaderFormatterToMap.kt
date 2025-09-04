package org.gudelker.rules

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class JsonReaderFormatterToMap(private val jsonPath: String) {
    fun jsonToMap(): Map<String, FormatterRule> {
        val gson = Gson()
        val json = File(jsonPath).readText()

        val type = object : TypeToken<Map<String, FormatterRule>>() {}.type
        return gson.fromJson(json, type)
    }
}
