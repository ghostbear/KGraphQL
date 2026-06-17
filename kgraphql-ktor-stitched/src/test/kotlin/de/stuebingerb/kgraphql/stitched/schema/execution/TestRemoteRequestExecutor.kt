package de.stuebingerb.kgraphql.stitched.schema.execution

import de.stuebingerb.kgraphql.Context
import de.stuebingerb.kgraphql.ExperimentalAPI
import de.stuebingerb.kgraphql.stitched.StitchedGraphqlRequest
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

@OptIn(ExperimentalAPI::class)
class TestRemoteRequestExecutor(private val client: HttpClient, val objectMapper: Json) :
    AbstractRemoteRequestExecutor(objectMapper) {
    override suspend fun executeRequest(url: String, request: StitchedGraphqlRequest, ctx: Context): String =
        client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(objectMapper.encodeToString(request))
        }.bodyAsText()
}

@OptIn(ExperimentalAPI::class)
class TestBrokenRemoteRequestExecutor(objectMapper: Json) : AbstractRemoteRequestExecutor(objectMapper) {
    override suspend fun executeRequest(url: String, request: StitchedGraphqlRequest, ctx: Context): String =
        throw SocketTimeoutException("Connection timed out")
}
