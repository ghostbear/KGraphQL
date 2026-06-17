package de.stuebingerb.kgraphql.serialization

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder

@InternalSerializationApi
public class JsonNodeSerializer(private val objectMapper: ObjectMapper) : KSerializer<JsonNode> {

    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JsonNode) {
        require(encoder is JsonEncoder) { "This serializer can only be used with JSON format" }

        val jsonString = objectMapper.writeValueAsString(value)
        val jsonElement = encoder.json.parseToJsonElement(jsonString)

        encoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): JsonNode {
        require(decoder is JsonDecoder) { "This serializer can only be used with JSON format" }

        val jsonElement = decoder.decodeJsonElement()

        return objectMapper.readTree(jsonElement.toString())
    }
}
