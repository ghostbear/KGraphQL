package de.stuebingerb.kgraphql.schema.execution

import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal suspend fun MutableMap<String, Deferred<JsonElement?>>.merge(
    key: String,
    node: Deferred<JsonElement?>
): MutableMap<String, Deferred<JsonElement?>> {
    merge(key, node, this::get, this::set)
    return this
}

// todo use return json object
internal fun JsonObject.merge(other: JsonObject): JsonObject {
    return JsonObject(this + other)
}
// todo use return json object
internal fun JsonObject.merge(key: String, node: JsonElement?): JsonObject {
    return this.merge(JsonObject(mapOf(key to (node ?: JsonNull))))
}

internal suspend fun merge(
    key: String,
    node: Deferred<JsonElement?>,
    get: (String) -> Deferred<JsonElement?>?,
    set: (String, Deferred<JsonElement?>) -> Any?
) {
    val existingNode = get(key)?.await()
    if (existingNode != null) {
        val node = node.await()
        when {
            node == null -> error("trying to merge null with non-null for $key")
            node is JsonObject -> {
                check(existingNode is JsonObject) { "trying to merge object with simple node for $key" }
                existingNode.merge(node)
            }

            existingNode is JsonObject -> error("trying to merge simple node with object node for $key")
            node != existingNode -> error("trying to merge different simple nodes for $key")
        }
    } else {
        set(key, node)
    }
}

internal fun merge(key: String, node: JsonElement?, get: (String) -> JsonElement?, set: (String, JsonElement?) -> Any?) {
    val existingNode = get(key)
    if (existingNode != null) {
        when {
            node == null -> error("trying to merge null with non-null for $key")
            node is JsonObject -> {
                check(existingNode is JsonObject) { "trying to merge object with simple node for $key" }
                existingNode.merge(node)
            }

            existingNode is JsonObject -> error("trying to merge simple node with object node for $key")
            node != existingNode -> error("trying to merge different simple nodes for $key")
        }
    } else {
        set(key, node)
    }
}
