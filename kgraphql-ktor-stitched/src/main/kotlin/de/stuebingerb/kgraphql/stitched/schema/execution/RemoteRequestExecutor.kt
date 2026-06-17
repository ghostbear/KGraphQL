package de.stuebingerb.kgraphql.stitched.schema.execution

import de.stuebingerb.kgraphql.Context
import de.stuebingerb.kgraphql.ExperimentalAPI
import de.stuebingerb.kgraphql.schema.execution.Execution
import kotlinx.serialization.json.JsonElement

/**
 * Interface for remote request execution, used during schema stitching (only)
 */
@ExperimentalAPI
interface RemoteRequestExecutor {
    // ParallelRequestExecutor expects a JsonNode as result of any execution
    suspend fun execute(node: Execution.Remote, ctx: Context): JsonElement?
}
