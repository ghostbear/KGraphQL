package de.stuebingerb.kgraphql.stitched

import de.stuebingerb.kgraphql.ExperimentalAPI
import kotlinx.serialization.json.JsonElement

@ExperimentalAPI
data class StitchedGraphqlRequest(
    val operationName: String? = null,
    val variables: JsonElement? = null,
    val query: String
)
