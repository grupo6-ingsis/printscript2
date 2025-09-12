package org.gudelker.rules

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class FormatterRuleDeserializer : JsonDeserializer<FormatterRule> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): FormatterRule {
        return when {
            json.isJsonObject -> {
                val obj = json.asJsonObject
                val on = obj.get("on").asBoolean
                val quantity = obj.get("quantity").asInt
                FormatterRule(on, quantity)
            }
            json.isJsonPrimitive && json.asJsonPrimitive.isBoolean -> {
                // Caso booleano
                FormatterRule(on = json.asBoolean, quantity = 1)
            }
            json.isJsonPrimitive && json.asJsonPrimitive.isNumber -> {
                // Caso número
                FormatterRule(on = true, quantity = json.asInt)
            }
            else -> throw JsonParseException("Formato de regla no soportado: $json")
        }
    }
}
