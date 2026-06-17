package de.stuebingerb.kgraphql.stitched.schema.dsl

import de.stuebingerb.kgraphql.ExperimentalAPI
import de.stuebingerb.kgraphql.schema.dsl.SchemaConfigurationDSL
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

    override fun build(): StitchedSchemaConfiguration {
        val json = Json(json) {
            if (useDefaultPrettyPrinter) {
                prettyPrint = true
                prettyPrintIndent = " ".repeat(2)
            }
        }
        return StitchedSchemaConfiguration(
            useCachingDocumentParser = useCachingDocumentParser,
            documentParserCacheMaximumSize = documentParserCacheMaximumSize,
            json = json,
            useDefaultPrettyPrinter = useDefaultPrettyPrinter,
            coroutineDispatcher = coroutineDispatcher,
            wrapErrors = wrapErrors,
            introspection = introspection,
            genericTypeResolver = genericTypeResolver,
            argumentTransformer = RemoteArgumentTransformer(json, genericTypeResolver),
            errorHandler = errorHandler,
            remoteExecutor = requireNotNull(remoteExecutor) { "Remote executor not defined" },
            localUrl = localUrl
        )
    }
}
