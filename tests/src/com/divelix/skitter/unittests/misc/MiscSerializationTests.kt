package com.divelix.skitter.unittests.misc

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.GdxFloatArraySerializer
import com.divelix.skitter.GdxIntArraySerializer
import ktx.collections.GdxFloatArray
import ktx.collections.GdxIntArray
import ktx.json.fromJson
import ktx.json.setSerializer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MiscSerializationTests {
    private lateinit var json: Json
    private lateinit var printSettings: JsonValue.PrettyPrintSettings

    @Before
    fun setup() {
        json = Json().apply {
            setUsePrototypes(false) // to not erase default values (false, 0)
            setSerializer(GdxIntArraySerializer())
            setSerializer(GdxFloatArraySerializer())
        }
        printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
    }

    @Test
    fun `check GdxIntArray serialization`() {
        val inputObj = GdxIntArray(intArrayOf(1, 2, 3, 4, 5))
        val refStr = "[ 1, 2, 3, 4, 5 ]"
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        println(prettyOutputStr)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check GdxIntArray deserialization`() {
        val inputStr = "[ 1, 2, 3, 4, 5 ]"
        val outputObj = json.fromJson<GdxIntArray>(inputStr)
        val refObj = GdxIntArray(intArrayOf(1, 2, 3, 4, 5))
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check GdxFloatArray serialization`() {
        val inputObj = GdxFloatArray(floatArrayOf(1.1f, 2.22f, 3.333f, 4.4444f, 5.55555f))
        val refStr = "[ 1.1, 2.22, 3.333, 4.4444, 5.55555 ]"
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        println(prettyOutputStr)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check GdxFloatArray deserialization`() {
        val inputStr = "[ 1.1, 2.22, 3.333, 4.4444, 5.55555 ]"
        val outputObj = json.fromJson<GdxFloatArray>(inputStr)
        val refObj = GdxFloatArray(floatArrayOf(1.1f, 2.22f, 3.333f, 4.4444f, 5.55555f))
        Assert.assertEquals(outputObj, refObj)
    }
}