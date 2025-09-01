package org.gudelker

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

class JsonLinterConfigLoaderToMap(val path: String) : LinterConfigLoader {
    override fun loadConfig(): Map<String, LinterConfig> {
        val gson = Gson()
        val json = File(path).readText()
        val jsonObject = gson.fromJson(json, JsonObject::class.java)
        val rulesJson = jsonObject.getAsJsonObject("rules")
        val config = gson.fromJson(rulesJson, LinterConfig::class.java)
        return rulesJson.keySet().associateWith { config }
    }
}
