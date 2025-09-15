package org.gudelker.rules

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class FormatterRuleDeserializer : JsonDeserializer<FormatterRule> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): FormatterRule {
        return when {
            json.isJsonPrimitive -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isBoolean -> FormatterRule(on = primitive.asBoolean, quantity = 1)
                    primitive.isNumber -> FormatterRule(on = true, quantity = primitive.asInt)
                    else -> FormatterRule(on = false, quantity = 0)
                }
            }
            json.isJsonObject -> {
                val obj = json.asJsonObject
                val on = obj.get("on")?.asBoolean ?: true
                val quantity = obj.get("quantity")?.asInt ?: 1
                FormatterRule(on = on, quantity = quantity)
            }
            else -> FormatterRule(on = false, quantity = 0)
        }
    }
}
