package de.stuebingerb.kgraphql.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@InternalSerializationApi
public class KotlinxJsonElementJacksonSerializer(private val json: Json) : JsonSerializer<JsonElement>() {
    override fun serialize(value: JsonElement, gen: JsonGenerator, serializers: SerializerProvider) {
        val jsonString = json.encodeToString(JsonElement.serializer(), value)
        gen.writeRawValue(jsonString)
    }
}
