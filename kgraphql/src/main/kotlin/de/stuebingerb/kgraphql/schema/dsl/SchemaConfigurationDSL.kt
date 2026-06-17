package de.stuebingerb.kgraphql.schema.dsl

import de.stuebingerb.kgraphql.configuration.SchemaConfiguration
import de.stuebingerb.kgraphql.schema.execution.ArgumentTransformer
import de.stuebingerb.kgraphql.schema.execution.ErrorHandler
import de.stuebingerb.kgraphql.schema.execution.GenericTypeResolver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

open class SchemaConfigurationDSL {
    var useDefaultPrettyPrinter: Boolean = false
    var useCachingDocumentParser: Boolean = true
    var json: Json = Json
    var documentParserCacheMaximumSize: Long = 1000L
    var acceptSingleValueAsArray: Boolean = true
    var coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
    var wrapErrors: Boolean = true
    var introspection: Boolean = true
    var genericTypeResolver: GenericTypeResolver = GenericTypeResolver.DEFAULT
    var errorHandler: ErrorHandler = ErrorHandler()

    fun update(block: SchemaConfigurationDSL.() -> Unit) = block()

    open fun build(): SchemaConfiguration {
        val json = Json(json) {
            if (useDefaultPrettyPrinter) {
                prettyPrint = true
                prettyPrintIndent = " ".repeat(2)
            }
        }

        return SchemaConfiguration(
            useCachingDocumentParser = useCachingDocumentParser,
            documentParserCacheMaximumSize = documentParserCacheMaximumSize,
            json = json,
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
