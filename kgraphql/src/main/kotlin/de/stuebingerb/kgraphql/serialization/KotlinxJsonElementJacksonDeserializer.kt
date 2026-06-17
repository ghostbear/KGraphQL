package de.stuebingerb.kgraphql.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@InternalSerializationApi
public class KotlinxJsonElementJacksonDeserializer(private val json: Json) : JsonDeserializer<JsonElement>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonElement {
        val node: JsonNode = p.codec.readTree(p)
        return json.parseToJsonElement(node.toString())
    }
}
