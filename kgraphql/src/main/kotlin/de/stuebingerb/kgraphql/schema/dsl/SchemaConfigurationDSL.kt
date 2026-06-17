package de.stuebingerb.kgraphql.schema.dsl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.stuebingerb.kgraphql.configuration.SchemaConfiguration
import de.stuebingerb.kgraphql.schema.execution.ArgumentTransformer
import de.stuebingerb.kgraphql.schema.execution.ErrorHandler
import de.stuebingerb.kgraphql.schema.execution.GenericTypeResolver
import de.stuebingerb.kgraphql.serialization.InternalSerializationApi
import de.stuebingerb.kgraphql.serialization.JsonNodeSerializer
import de.stuebingerb.kgraphql.serialization.registerKotlinxSerializationModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

open class SchemaConfigurationDSL {
    var useDefaultPrettyPrinter: Boolean = false
    var useCachingDocumentParser: Boolean = true
    val json: Json = Json
    var objectMapper: ObjectMapper = jacksonObjectMapper()
    var documentParserCacheMaximumSize: Long = 1000L
    var acceptSingleValueAsArray: Boolean = true
    var coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
    var wrapErrors: Boolean = true
    var introspection: Boolean = true
    var genericTypeResolver: GenericTypeResolver = GenericTypeResolver.DEFAULT
    var errorHandler: ErrorHandler = ErrorHandler()

    fun update(block: SchemaConfigurationDSL.() -> Unit) = block()

    @OptIn(InternalSerializationApi::class)
    open fun build(): SchemaConfiguration {
        val json = Json(json) {
            if (useDefaultPrettyPrinter) {
                prettyPrint = true
                prettyPrintIndent = " ".repeat(2)
            }
            serializersModule = SerializersModule {
                contextual(JsonNode::class, JsonNodeSerializer(objectMapper))
            }
        }
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, acceptSingleValueAsArray)
            .registerKotlinxSerializationModule(json)

        return SchemaConfiguration(
            useCachingDocumentParser = useCachingDocumentParser,
            documentParserCacheMaximumSize = documentParserCacheMaximumSize,
            json = json,
            objectMapper = objectMapper,
            useDefaultPrettyPrinter = useDefaultPrettyPrinter,
            coroutineDispatcher = coroutineDispatcher,
            wrapErrors = wrapErrors,
            introspection = introspection,
            genericTypeResolver = genericTypeResolver,
            argumentTransformer = ArgumentTransformer(genericTypeResolver),
            errorHandler = errorHandler
        )
    }
}
