package de.stuebingerb.kgraphql.merge

import de.stuebingerb.kgraphql.expect
import de.stuebingerb.kgraphql.schema.execution.merge
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test

class ObjectNodeMergeTest {

    @Test
    fun `merge should add property`() {
        val existing = JsonObject(mapOf("param1" to JsonPrimitive("value1")))
        val update = JsonObject(mapOf("param2" to JsonPrimitive("value2")))

        existing.merge(update)

        val expected = JsonPrimitive("value2")
        existing["param2"] shouldBe expected
    }

    @Test
    fun `merge should add nested property`() {
        val existing = JsonObject(mapOf("param1" to JsonPrimitive("value1")))
        val update = JsonObject(mapOf("sub" to JsonObject(mapOf("param2" to JsonPrimitive("value2")))))
        existing.merge(update)

        val expected = JsonObject(mapOf("param2" to JsonPrimitive("value2")))
        existing["sub"] shouldBe expected
    }

    @Test
    fun `merge should not change simple node`() {
        val existing = JsonObject(mapOf("param" to JsonPrimitive("value1")))
        val update = JsonObject(mapOf("param" to JsonPrimitive("value2")))

        expect<IllegalStateException>("trying to merge different simple nodes for param") {
            existing.merge(update)
        }

        val expected = JsonPrimitive("value1")
        existing["param"] shouldBe expected
    }

    @Test
    fun `merge should not merge simple node with object node`() {
        val existing = JsonObject(mapOf("param" to JsonPrimitive("value1")))
        val update = JsonObject(mapOf("param" to JsonObject(emptyMap())))

        expect<IllegalStateException>("trying to merge object with simple node for param") {
            existing.merge(update)
        }

        val expected = JsonPrimitive("value1")
        existing["param"] shouldBe expected
    }

    @Test
    fun `merge should not merge object node with simple node`() {
        val existing = JsonObject(mapOf("param" to JsonObject(mapOf("other" to JsonPrimitive("value1")))))
        val update = JsonObject(mapOf("param" to JsonPrimitive("value2")))

        expect<IllegalStateException>("trying to merge simple node with object node for param") {
            existing.merge(update)
        }

        existing["param"] shouldBe existing
    }
}
