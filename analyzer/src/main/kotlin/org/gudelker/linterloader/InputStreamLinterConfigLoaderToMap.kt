package org.gudelker.linterloader

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.gudelker.LinterConfig
import java.io.InputStream
import java.io.InputStreamReader

class InputStreamLinterConfigLoaderToMap(private val inputStream: InputStream) : LinterConfigLoader {
    override fun loadConfig(): Map<String, LinterConfig> {
        val gson = Gson()
        val jsonObject = gson.fromJson(InputStreamReader(inputStream), JsonObject::class.java)
        val rulesJson = jsonObject.getAsJsonObject("rules")
        return rulesJson.keySet().associateWith { key ->
            gson.fromJson(rulesJson.getAsJsonObject(key), LinterConfig::class.java)
        }
    }
}
