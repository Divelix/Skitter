package com.divelix.skitter.unittests.localdata

import com.badlogic.gdx.utils.FloatArray
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.GunModSerializer
import com.divelix.skitter.ShipModSerializer
import com.divelix.skitter.data.GunMod
import com.divelix.skitter.data.GunModEffects
import com.divelix.skitter.data.ShipMod
import com.divelix.skitter.data.ShipModEffects
import ktx.collections.gdxMapOf
import ktx.json.fromJson
import ktx.json.setSerializer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ModDataTests {
    private lateinit var json: Json
    private lateinit var printSettings: JsonValue.PrettyPrintSettings

    @Before
    fun setup() {
        json = Json().apply {
            setUsePrototypes(false) // to not erase default values (false, 0)
            setSerializer(ShipModSerializer())
            setSerializer(GunModSerializer())
        }
        printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
    }

    @Test
    fun `check ShipMod serialization`() {
        val inputObj = ShipMod(13, "SHIP_MOD_NAME", gdxMapOf(
                ShipModEffects.HealthBooster to FloatArray(floatArrayOf(1f, 2f))
        ))
        val refStr =
                """
                    {
                    "index": 13,
                    "name": "SHIP_MOD_NAME",
                    "effects": {
                    	"HealthBooster": [ 1, 2 ]
                    }
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check ShipMod deserialization`() {
        val inputStr =
                """
                    {
                    "index": 13,
                    "name": "SHIP_MOD_NAME",
                    "effects": {
                    	"HealthBooster": [ 1, 2 ]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<ShipMod>(inputStr)
        val refObj = ShipMod(13, "SHIP_MOD_NAME", gdxMapOf(
                ShipModEffects.HealthBooster to FloatArray(floatArrayOf(1f, 2f))
        ))
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check GunMod serialization`() {
        val inputObj = GunMod(15, "GUN_MOD_NAME", gdxMapOf(
                GunModEffects.DamageBooster to FloatArray(floatArrayOf(1f, 2f))
        ))
        val refStr =
                """
                    {
                    "index": 15,
                    "name": "GUN_MOD_NAME",
                    "effects": {
                    	"DamageBooster": [ 1, 2 ]
                    }
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check GunMod deserialization`() {
        val inputStr =
                """
                    {
                    "index": 15,
                    "name": "GUN_MOD_NAME",
                    "effects": {
                    	"DamageBooster": [ 1, 2 ]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<GunMod>(inputStr)
        val refObj = GunMod(15, "GUN_MOD_NAME", gdxMapOf(
                GunModEffects.DamageBooster to FloatArray(floatArrayOf(1f, 2f))
        ))
        Assert.assertEquals(outputObj, refObj)
    }
}