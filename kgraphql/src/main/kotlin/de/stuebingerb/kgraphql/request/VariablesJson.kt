package de.stuebingerb.kgraphql.request

import de.stuebingerb.kgraphql.helpers.toValueNode
import de.stuebingerb.kgraphql.schema.model.ast.NameNode
import de.stuebingerb.kgraphql.schema.model.ast.ValueNode
import de.stuebingerb.kgraphql.schema.structure.Type
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject

/**
 * Represents already parsed variables json
 */
interface VariablesJson {

    fun get(type: Type, key: NameNode): ValueNode?

    fun getRaw(): JsonElement?

    class Empty : VariablesJson {
        override fun get(type: Type, key: NameNode): ValueNode? = null

        override fun getRaw(): JsonElement? = null
    }

    class Defined(val json: JsonElement) : VariablesJson {
        override fun get(type: Type, key: NameNode): ValueNode? {
            return json.jsonObject.let { node -> node[key.value] }?.toValueNode(type)
        }

        /**
         * Returns the raw [json] unless it is a [JsonNull]
         */
        override fun getRaw(): JsonElement? = json.takeUnless { it is JsonNull }
    }
}
