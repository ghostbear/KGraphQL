package de.stuebingerb.kgraphql.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@InternalSerializationApi
public class KotlinxSerializationModule(json: Json) : SimpleModule("KotlinxSerializationModule") {
    init {
        addSerializer(JsonElement::class.java, KotlinxJsonElementJacksonSerializer(json))
        addDeserializer(JsonElement::class.java, KotlinxJsonElementJacksonDeserializer(json))
    }
}

@InternalSerializationApi
public fun ObjectMapper.registerKotlinxSerializationModule(json: Json): ObjectMapper = registerModule(KotlinxSerializationModule(json))
