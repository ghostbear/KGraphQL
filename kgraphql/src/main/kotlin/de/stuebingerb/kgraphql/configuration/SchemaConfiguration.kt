package de.stuebingerb.kgraphql.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import de.stuebingerb.kgraphql.schema.execution.ArgumentTransformer
import de.stuebingerb.kgraphql.schema.execution.ErrorHandler
import de.stuebingerb.kgraphql.schema.execution.GenericTypeResolver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

open class SchemaConfiguration(
    // document parser caching mechanisms
    val useCachingDocumentParser: Boolean,
    val documentParserCacheMaximumSize: Long,
    val json: Json,
    // jackson features
    val objectMapper: ObjectMapper,
    val useDefaultPrettyPrinter: Boolean,
    // execution
    val coroutineDispatcher: CoroutineDispatcher,
    val wrapErrors: Boolean,
    // allow schema introspection
    val introspection: Boolean = true,
    val genericTypeResolver: GenericTypeResolver,
    val argumentTransformer: ArgumentTransformer,
    val errorHandler: ErrorHandler
)
