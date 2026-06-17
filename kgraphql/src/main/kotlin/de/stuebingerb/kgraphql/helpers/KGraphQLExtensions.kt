package de.stuebingerb.kgraphql.helpers

import de.stuebingerb.kgraphql.ValidationException
import de.stuebingerb.kgraphql.schema.execution.Execution
import de.stuebingerb.kgraphql.schema.introspection.TypeKind
import de.stuebingerb.kgraphql.schema.introspection.__Type
import de.stuebingerb.kgraphql.schema.model.ast.NameNode
import de.stuebingerb.kgraphql.schema.model.ast.ValueNode
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import java.awt.SystemColor.text

/**
 * This returns a list of all scalar fields requested on this type.
 */
fun Execution.getFields(): List<String> = when (this) {
    is Execution.Fragment -> elements.flatMap(Execution::getFields)
    is Execution.Node -> {
        if (children.isEmpty()) {
            listOf(key)
        } else {
            children
                .filterNot { it is Execution.Node && it.children.isNotEmpty() }
                .flatMap(Execution::getFields)
        }
    }
}.distinct()

/**
 * Collection : Convert to JsonElement
 */
private fun Collection<*>.toJsonElement(): JsonElement {
    val list: MutableList<JsonElement> = mutableListOf()
    forEach {
        val value = it ?: return@forEach
        when (value) {
            is Number -> list.add(JsonPrimitive(value))
            is Boolean -> list.add(JsonPrimitive(value))
            is String -> list.add(JsonPrimitive(value))
            is Map<*, *> -> list.add(value.toJsonElement())
            is Collection<*> -> list.add(value.toJsonElement())
            is Array<*> -> list.add(value.toList().toJsonElement())
            else -> list.add(JsonPrimitive(value.toString())) // other type
        }
    }
    return JsonArray(list)
}

/**
 * Map : Convert to JsonElement
 */
fun Map<*, *>.toJsonElement(): JsonElement {
    val map: MutableMap<String, JsonElement> = mutableMapOf()
    forEach {
        val key = it.key as? String ?: return@forEach
        val value = it.value ?: return@forEach
        when (value) {
            is Number -> map[key] = JsonPrimitive(value)
            is Boolean -> map[key] = JsonPrimitive(value)
            is String -> map[key] = JsonPrimitive(value)
            is Map<*, *> -> map[key] = value.toJsonElement()
            is Collection<*> -> map[key] = value.toJsonElement()
            is Array<*> -> map[key] = value.toList().toJsonElement()
            else -> map[key] = JsonPrimitive(value.toString())  // other type
        }
    }
    return JsonObject(map)
}

/**
 * Converts this [JsonNode] to a [ValueNode], applying special treatment for doubles and floats, as specified by
 * https://spec.graphql.org/October2021/#sec-Scalars.Input-Coercion:
 *
 * "GraphQL has different constant literals to represent integer and floating-point input values, and coercion rules
 * may apply differently depending on which type of input value is encountered. GraphQL may be parameterized by
 * variables, the values of which are often serialized when sent over a transport like HTTP. Since some common
 * serializations (ex. JSON) do not discriminate between integer and floating-point values, they are interpreted as an
 * integer input value if they have an empty fractional part (ex. 1.0) and otherwise as floating-point input value."
 */
fun JsonElement?.toValueNode(expectedType: __Type): ValueNode {
    when (this) {
        is JsonNull, null -> return ValueNode.NullValueNode(null)
        is JsonArray -> return ValueNode.ListValueNode(this.map { it.toValueNode(expectedType) }, null)
        is JsonObject -> return ValueNode.ObjectValueNode(
            this.filterNot { it.key.startsWith("__") }.map { prop ->
                val inputFields = checkNotNull(expectedType.unwrapped().inputFields) {
                    "Expected INPUT_OBJECT for ${expectedType.unwrapped().name} but got ${expectedType.kind}"
                }
                val expectedPropType = inputFields.firstOrNull { it.name == prop.key }?.type
                    ?: throw ValidationException(
                        "Property '${prop.key}' on '${expectedType.unwrapped().name}' does not exist"
                    )
                ValueNode.ObjectValueNode.ObjectFieldNode(
                    null,
                    NameNode(prop.key, null),
                    prop.value.toValueNode(expectedPropType)
                )
            },
            null
        )

        is JsonPrimitive -> {
            val booleanOrNull = booleanOrNull
            if (booleanOrNull != null) {
                return ValueNode.BooleanValueNode(booleanOrNull, null)
            }
            val longOrNull = longOrNull ?: intOrNull?.toLong()
            if (longOrNull != null) {
                return ValueNode.NumberValueNode(longOrNull, null)
            }
            val doubleOrNull = doubleOrNull ?: floatOrNull?.toDouble()
            if (doubleOrNull != null) {
                if (doubleOrNull.isWholeNumber()) {
                    return ValueNode.NumberValueNode(doubleOrNull.toLong(), null)
                }
                return ValueNode.DoubleValueNode(doubleOrNull, null)
            }
            val text = content
            if (expectedType.unwrapped().kind == TypeKind.ENUM) {
                return ValueNode.EnumValueNode(text, null)
            }
            return ValueNode.StringValueNode(text, false, null)
        }
    }
}

internal fun Double.isWholeNumber() = this % 1.0 == 0.0
