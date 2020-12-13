package com.divelix.skitter.unittests.localdata

import com.badlogic.gdx.utils.FloatArray
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.GdxFloatArraySerializer
import com.divelix.skitter.GdxIntArraySerializer
import com.divelix.skitter.GunModSerializer
import com.divelix.skitter.ShipModSerializer
import com.divelix.skitter.data.*
import ktx.collections.GdxIntArray
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
//            setSerializer(GdxFloatArraySerializer())
            setSerializer(GdxIntArraySerializer())
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

    @Test
    fun `check Mods serialization`() {
        val inputObj = Mods(
                gdxArrayOf(
                        ShipMod(11, "SHIP_MOD_NAME 1", gdxMapOf(
                                ShipModEffects.HealthBooster to FloatArray(floatArrayOf(1f, 2f))
                        )),
                        ShipMod(12, "SHIP_MOD_NAME 2", gdxMapOf(
                                ShipModEffects.HealthBooster to FloatArray(floatArrayOf(3f, 4f))
                        ))
                ),
                gdxArrayOf(
                        GunMod(13, "GUN_MOD_NAME 1", gdxMapOf(
                                GunModEffects.DamageBooster to FloatArray(floatArrayOf(1f, 2f))
                        )),
                        GunMod(14, "GUN_MOD_NAME 2", gdxMapOf(
                                GunModEffects.DamageBooster to FloatArray(floatArrayOf(3f, 4f))
                        ))
                )
        )
        val refStr =
                """
                    {
                    "ship": [
                    	{
                    		"index": 11,
                    		"name": "SHIP_MOD_NAME 1",
                    		"effects": {
                    			"HealthBooster": [ 1, 2 ]
                    		}
                    	},
                    	{
                    		"index": 12,
                    		"name": "SHIP_MOD_NAME 2",
                    		"effects": {
                    			"HealthBooster": [ 3, 4 ]
                    		}
                    	}
                    ],
                    "gun": [
                    	{
                    		"index": 13,
                    		"name": "GUN_MOD_NAME 1",
                    		"effects": {
                    			"DamageBooster": [ 1, 2 ]
                    		}
                    	},
                    	{
                    		"index": 14,
                    		"name": "GUN_MOD_NAME 2",
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
    fun `check Mods deserialization`() {
        val inputStr =
                """
                    {
                    "ship": [
                    	{
                    		"index": 11,
                    		"name": "SHIP_MOD_NAME 1",
                    		"effects": {
                    			"HealthBooster": [ 1, 2 ]
                    		}
                    	},
                    	{
                    		"index": 12,
                    		"name": "SHIP_MOD_NAME 2",
                    		"effects": {
                    			"HealthBooster": [ 3, 4 ]
                    		}
                    	}
                    ],
                    "gun": [
                    	{
                    		"index": 13,
                    		"name": "GUN_MOD_NAME 1",
                    		"effects": {
                    			"DamageBooster": [ 1, 2 ]
                    		}
                    	},
                    	{
                    		"index": 14,
                    		"name": "GUN_MOD_NAME 2",
                    		"effects": {
                    			"DamageBooster": [ 3, 4 ]
                    		}
                    	}
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<Mods>(inputStr)
        val refObj = Mods(
                gdxArrayOf(
                        ShipMod(11, "SHIP_MOD_NAME 1", gdxMapOf(
                                ShipModEffects.HealthBooster to FloatArray(floatArrayOf(1f, 2f))
                        )),
                        ShipMod(12, "SHIP_MOD_NAME 2", gdxMapOf(
                                ShipModEffects.HealthBooster to FloatArray(floatArrayOf(3f, 4f))
                        ))
                ),
                gdxArrayOf(
                        GunMod(13, "GUN_MOD_NAME 1", gdxMapOf(
                                GunModEffects.DamageBooster to FloatArray(floatArrayOf(1f, 2f))
                        )),
                        GunMod(14, "GUN_MOD_NAME 2", gdxMapOf(
                                GunModEffects.DamageBooster to FloatArray(floatArrayOf(3f, 4f))
                        ))
                )
        )
        Assert.assertEquals(outputObj, refObj)
    }



    @Test
    fun `check ModsData serialization`() {
        val inputObj = ModsData(
                IntArray(intArrayOf(1, 2, 3, 4, 5)),
                IntArray(intArrayOf(6, 7, 8, 9, 10)),
                Mods(
                        gdxArrayOf(
                                ShipMod(11, "SHIP_MOD_NAME 1", gdxMapOf(
                                        ShipModEffects.HealthBooster to FloatArray(floatArrayOf(1f, 2f))
                                )),
                                ShipMod(12, "SHIP_MOD_NAME 2", gdxMapOf(
                                        ShipModEffects.HealthBooster to FloatArray(floatArrayOf(3f, 4f))
                                ))
                        ),
                        gdxArrayOf(
                                GunMod(13, "GUN_MOD_NAME 1", gdxMapOf(
                                        GunModEffects.DamageBooster to FloatArray(floatArrayOf(1f, 2f))
                                )),
                                GunMod(14, "GUN_MOD_NAME 2", gdxMapOf(
                                        GunModEffects.DamageBooster to FloatArray(floatArrayOf(3f, 4f))
                                ))
                        )
                )
        )
        val refStr =
                """
                    {
                    "sellPrices": [ 1, 2, 3, 4, 5 ],
                    "upgradePrices": [ 6, 7, 8, 9, 10 ],
                    "mods": {
                    	"ship": [
                    		{
                    			"index": 11,
                    			"name": "SHIP_MOD_NAME 1",
                    			"effects": {
                    				"HealthBooster": [ 1, 2 ]
                    			}
                    		},
                    		{
                    			"index": 12,
                    			"name": "SHIP_MOD_NAME 2",
                    			"effects": {
                    				"HealthBooster": [ 3, 4 ]
                    			}
                    		}
                    	],
                    	"gun": [
                    		{
                    			"index": 13,
                    			"name": "GUN_MOD_NAME 1",
                    			"effects": {
                    				"DamageBooster": [ 1, 2 ]
                    			}
                    		},
                    		{
                    			"index": 14,
                    			"name": "GUN_MOD_NAME 2",
                    			"effects": {
                    				"DamageBooster": [ 3, 4 ]
                    			}
                    		}
                    	]
                    }
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
                    "mods": {
                    	"ship": [
                    		{
                    			"index": 11,
                    			"name": "SHIP_MOD_NAME 1",
                    			"effects": {
                    				"HealthBooster": [ 1, 2 ]
                    			}
                    		},
                    		{
                    			"index": 12,
                    			"name": "SHIP_MOD_NAME 2",
                    			"effects": {
                    				"HealthBooster": [ 3, 4 ]
                    			}
                    		}
                    	],
                    	"gun": [
                    		{
                    			"index": 13,
                    			"name": "GUN_MOD_NAME 1",
                    			"effects": {
                    				"DamageBooster": [ 1, 2 ]
                    			}
                    		},
                    		{
                    			"index": 14,
                    			"name": "GUN_MOD_NAME 2",
                    			"effects": {
                    				"DamageBooster": [ 3, 4 ]
                    			}
                    		}
                    	]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<ModsData>(inputStr)
        val refObj = ModsData(
                IntArray(intArrayOf(1, 2, 3, 4, 5)),
                IntArray(intArrayOf(6, 7, 8, 9, 10)),
                Mods(
                        gdxArrayOf(
                                ShipMod(11, "SHIP_MOD_NAME 1", gdxMapOf(
                                        ShipModEffects.HealthBooster to FloatArray(floatArrayOf(1f, 2f))
                                )),
                                ShipMod(12, "SHIP_MOD_NAME 2", gdxMapOf(
                                        ShipModEffects.HealthBooster to FloatArray(floatArrayOf(3f, 4f))
                                ))
                        ),
                        gdxArrayOf(
                                GunMod(13, "GUN_MOD_NAME 1", gdxMapOf(
                                        GunModEffects.DamageBooster to FloatArray(floatArrayOf(1f, 2f))
                                )),
                                GunMod(14, "GUN_MOD_NAME 2", gdxMapOf(
                                        GunModEffects.DamageBooster to FloatArray(floatArrayOf(3f, 4f))
                                ))
                        )
                )
        )
        Assert.assertEquals(outputObj, refObj)
    }
}