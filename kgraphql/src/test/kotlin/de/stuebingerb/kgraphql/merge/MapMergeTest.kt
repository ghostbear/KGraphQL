package de.stuebingerb.kgraphql.merge

import de.stuebingerb.kgraphql.expect
import de.stuebingerb.kgraphql.schema.execution.merge
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test

class MapMergeTest {

    @Test
    fun `merge should add property`() {
        runBlocking {
            val existing = createMap("param1" to CompletableDeferred(JsonPrimitive("value1")))
            val update = CompletableDeferred(JsonPrimitive("value2"))

            existing.merge("param2", update)

            existing["param2"] shouldBe update
        }
    }

    @Test
    fun `merge should add nested property`() {
        runBlocking {
            val existing = createMap("param1" to CompletableDeferred(JsonPrimitive("value1")))
            val update = CompletableDeferred(JsonObject(mapOf("param2" to JsonPrimitive("value2"))))

            existing.merge("sub", update)

            existing["sub"] shouldBe update
        }
    }

    @Test
    fun `merge should not change simple node`() {
        runBlocking {
            val existingValue = CompletableDeferred(JsonPrimitive("value1"))
            val existing = createMap("param" to existingValue)
            val update = CompletableDeferred(JsonPrimitive("value2"))

            expect<IllegalStateException>("trying to merge different simple nodes for param") {
                existing.merge(
                    "param",
                    update
                )
            }

            existing["param"] shouldBe existingValue
        }
    }

    @Test
    fun `merge should not merge simple node with object node`() {
        runBlocking {
            val existingValue = CompletableDeferred(JsonPrimitive("value1"))
            val existing = createMap("param" to existingValue)
            val update = CompletableDeferred(JsonObject(emptyMap()))

            expect<IllegalStateException>("trying to merge object with simple node for param") {
                existing.merge(
                    "param",
                    update
                )
            }

            val expected = JsonPrimitive("value1")
            existing["param"]?.await() shouldBe expected
        }
    }

    @Test
    fun `merge should not merge object node with simple node`() {
        runBlocking {
            val existingObj = CompletableDeferred(JsonObject(mapOf("other" to JsonPrimitive("value1"))))
            val existing = createMap("param" to existingObj)
            val update = CompletableDeferred(JsonPrimitive("value2"))

            expect<IllegalStateException>("trying to merge simple node with object node for param") {
                existing.merge(
                    "param",
                    update
                )
            }

            existing["param"] shouldBe existingObj
        }
    }

    private fun createMap(vararg pairs: Pair<String, Deferred<JsonElement?>>) = mutableMapOf(*pairs)
}
