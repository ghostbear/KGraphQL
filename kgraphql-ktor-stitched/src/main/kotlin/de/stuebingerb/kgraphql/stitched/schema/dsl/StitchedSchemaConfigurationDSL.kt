package de.stuebingerb.kgraphql.stitched.schema.dsl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import de.stuebingerb.kgraphql.ExperimentalAPI
import de.stuebingerb.kgraphql.schema.dsl.SchemaConfigurationDSL
import de.stuebingerb.kgraphql.serialization.InternalSerializationApi
import de.stuebingerb.kgraphql.serialization.JsonNodeSerializer
import de.stuebingerb.kgraphql.serialization.registerKotlinxSerializationModule
import de.stuebingerb.kgraphql.stitched.schema.configuration.StitchedSchemaConfiguration
import de.stuebingerb.kgraphql.stitched.schema.execution.RemoteArgumentTransformer
import de.stuebingerb.kgraphql.stitched.schema.execution.RemoteRequestExecutor
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@ExperimentalAPI
open class StitchedSchemaConfigurationDSL : SchemaConfigurationDSL() {
    // Remote executor has to be set for remote schemas
    var remoteExecutor: RemoteRequestExecutor? = null

    // Local url has to be set when stitching local queries (only)
    var localUrl: String? = null

    @OptIn(InternalSerializationApi::class)
    override fun build(): StitchedSchemaConfiguration {
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
        return StitchedSchemaConfiguration(
            useCachingDocumentParser = useCachingDocumentParser,
            documentParserCacheMaximumSize = documentParserCacheMaximumSize,
            json = json,
            objectMapper = objectMapper,
            useDefaultPrettyPrinter = useDefaultPrettyPrinter,
            coroutineDispatcher = coroutineDispatcher,
            wrapErrors = wrapErrors,
            introspection = introspection,
            genericTypeResolver = genericTypeResolver,
            argumentTransformer = RemoteArgumentTransformer(objectMapper, genericTypeResolver),
            errorHandler = errorHandler,
            remoteExecutor = requireNotNull(remoteExecutor) { "Remote executor not defined" },
            localUrl = localUrl
        )
    }
}
