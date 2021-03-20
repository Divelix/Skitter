package com.divelix.skitter.unittests.localdata

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.*
import com.divelix.skitter.data.*
import ktx.collections.gdxArrayOf
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
            setSerializer(GdxFloatArraySerializer())
            setSerializer(GdxIntArraySerializer())
            setSerializer(ModSerializer())
//            setSerializer(ShipModSerializer())
//            setSerializer(GunModSerializer())
        }
        printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
    }

    @Test
    fun `check Mod serialization`() {
        val inputObj = Mod(
                ModType.GUN_MOD,
                345,
                "asdas",
                gdxMapOf(ModEffect.GunModEffect.DamageBooster to gdxFloatArrayOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f))
        )
        val refStr =
                """
                    {
                    "type": "gun_mod",
                    "index": 345,
                    "name": "asdas",
                    "effects": {
                    	"DamageBooster": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 ]
                    }
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check Mod deserialization`() {
        val inputStr =
                """
                    {
                    "type": "gun_mod",
                    "index": 345,
                    "name": "asdas",
                    "effects": {
                    	"DamageBooster": [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 ]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<Mod>(inputStr)
        val refObj = Mod(
                ModType.GUN_MOD,
                345,
                "asdas",
                gdxMapOf(ModEffect.GunModEffect.DamageBooster to gdxFloatArrayOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f))
        )
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check ModsData serialization`() {
        val inputObj = ModsData(
                gdxIntArrayOf(1, 2, 3, 4, 5),
                gdxIntArrayOf(6, 7, 8, 9, 10),
                gdxArrayOf(
                        Mod(ModType.SHIP_MOD, 1, "Chubby", gdxMapOf(
                                ModEffect.ShipModEffect.HealthBooster to gdxFloatArrayOf(1f, 2f)
                        )),
                        Mod(ModType.GUN_MOD, 2, "Killer", gdxMapOf(
                                ModEffect.GunModEffect.DamageBooster to gdxFloatArrayOf(3f, 4f)
                        ))
                )
        )
        val refStr =
                """
                    {
                    "sellPrices": [ 1, 2, 3, 4, 5 ],
                    "upgradePrices": [ 6, 7, 8, 9, 10 ],
                    "mods": [
                    	{
                    		"type": "ship_mod",
                    		"index": 1,
                    		"name": "Chubby",
                    		"effects": {
                    			"HealthBooster": [ 1, 2 ]
                    		}
                    	},
                    	{
                    		"type": "gun_mod",
                    		"index": 2,
                    		"name": "Killer",
                    		"effects": {
                    			"DamageBooster": [ 3, 4 ]
                    		}
                    	}
                    ]
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check ModsData deserialization`() {
        val inputStr =
                """
                    {
                    "sellPrices": [ 1, 2, 3, 4, 5 ],
                    "upgradePrices": [ 6, 7, 8, 9, 10 ],
                    "mods": [
                    	{
                    		"type": "ship_mod",
                    		"index": 1,
                    		"name": "Chubby",
                    		"effects": {
                    			"HealthBooster": [ 1, 2 ]
                    		}
                    	},
                    	{
                    		"type": "gun_mod",
                    		"index": 2,
                    		"name": "Killer",
                    		"effects": {
                    			"DamageBooster": [ 3, 4 ]
                    		}
                    	}
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<ModsData>(inputStr)
        val refObj = ModsData(
                gdxIntArrayOf(1, 2, 3, 4, 5),
                gdxIntArrayOf(6, 7, 8, 9, 10),
                gdxArrayOf(
                        Mod(ModType.SHIP_MOD, 1, "Chubby", gdxMapOf(
                                ModEffect.ShipModEffect.HealthBooster to gdxFloatArrayOf(1f, 2f)
                        )),
                        Mod(ModType.GUN_MOD, 2, "Killer", gdxMapOf(
                                ModEffect.GunModEffect.DamageBooster to gdxFloatArrayOf(3f, 4f)
                        ))
                )
        )
        Assert.assertEquals(outputObj, refObj)
    }
}