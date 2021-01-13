package com.divelix.skitter.unittests.localdata

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.EquipSerializer
import com.divelix.skitter.GdxFloatArraySerializer
import com.divelix.skitter.data.*
import com.divelix.skitter.gdxFloatArrayOf
import ktx.collections.GdxFloatArray
import ktx.collections.gdxArrayOf
import ktx.json.fromJson
import ktx.json.setSerializer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EquipDataTests {
    private lateinit var json: Json
    private lateinit var printSettings: JsonValue.PrettyPrintSettings

    @Before
    fun setup() {
        json = Json().apply {
            setUsePrototypes(false) // to not erase default values (false, 0)
            setSerializer(GdxFloatArraySerializer())
            setSerializer(EquipSerializer())
//            setSerializer(ShipSerializer())
//            setSerializer(GunSerializer())
        }
        printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
    }

    @Test
    fun `check Equip serialization`() {
        val inputObj = Equip(EquipType.SHIP, 1, "DefaultShip", ShipSpecs(
                gdxFloatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f),
                gdxFloatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f)
        ))
        val refStr =
                """
                    {
                    "type": "ship",
                    "index": 1,
                    "name": "DefaultShip",
                    "specs": {
                    	"health": [ 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.1 ],
                    	"speed": [ 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.1 ]
                    }
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(refStr, prettyOutputStr)
    }

    @Test
    fun `check Equip deserialization`() {
        val inputStr =
                """
                    {
                    "type": "ship",
                    "index": 1,
                    "name": "DefaultShip",
                    "specs": {
                    	"health": [ 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.1 ],
                    	"speed": [ 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.1 ]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<Equip>(inputStr)
        val refObj = Equip(EquipType.SHIP, 1, "DefaultShip", ShipSpecs(
                gdxFloatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f),
                gdxFloatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f)
        ))
        Assert.assertEquals(refObj, outputObj)
    }

    @Test
    fun `check EquipsData serialization`() {
        val inputObj = EquipsData(
                gdxArrayOf(
                        Equip(EquipType.SHIP, 1, "DefaultShip", ShipSpecs(
                                gdxFloatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f),
                                gdxFloatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f)
                        )),
                        Equip(EquipType.GUN, 1, "DefaultGun", GunSpecs(
                                gdxFloatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f),
                                gdxFloatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f),
                                gdxFloatArrayOf(3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f, 3.10f),
                                gdxFloatArrayOf(4.1f, 4.2f, 4.3f, 4.4f, 4.5f, 4.6f, 4.7f, 4.8f, 4.9f, 4.10f),
                                gdxFloatArrayOf(5.1f, 5.2f, 5.3f, 5.4f, 5.5f, 5.6f, 5.7f, 5.8f, 5.9f, 5.10f),
                                gdxFloatArrayOf(6.1f, 6.2f, 6.3f, 6.4f, 6.5f, 6.6f, 6.7f, 6.8f, 6.9f, 6.10f)
                        ))
                )
        )
        val refStr =
                """
                    {
                    "equips": [
                    	{
                    		"type": "ship",
                    		"index": 1,
                    		"name": "DefaultShip",
                    		"specs": {
                    			"health": [ 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.1 ],
                    			"speed": [ 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.1 ]
                    		}
                    	},
                    	{
                    		"type": "gun",
                    		"index": 1,
                    		"name": "DefaultGun",
                    		"specs": {
                    			"damage": [ 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.1 ],
                    			"capacity": [ 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.1 ],
                    			"reload": [ 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9, 3.1 ],
                    			"speed": [ 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 4.9, 4.1 ],
                    			"crit": [ 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 5.9, 5.1 ],
                    			"chance": [ 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8, 6.9, 6.1 ]
                    		}
                    	}
                    ]
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        println(prettyOutputStr)
        Assert.assertEquals(refStr, prettyOutputStr)
    }

    @Test
    fun `check EquipsData deserialization`() {
        val inputStr =
                """
                    {
                    "equips": [
                    	{
                    		"type": "ship",
                    		"index": 1,
                    		"name": "DefaultShip",
                    		"specs": {
                    			"health": [ 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.1 ],
                    			"speed": [ 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.1 ]
                    		}
                    	},
                    	{
                    		"type": "gun",
                    		"index": 1,
                    		"name": "DefaultGun",
                    		"specs": {
                    			"damage": [ 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.1 ],
                    			"capacity": [ 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.1 ],
                    			"reload": [ 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9, 3.1 ],
                    			"speed": [ 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 4.9, 4.1 ],
                    			"crit": [ 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 5.8, 5.9, 5.1 ],
                    			"chance": [ 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8, 6.9, 6.1 ]
                    		}
                    	}
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<EquipsData>(inputStr)
        val refObj = EquipsData(
                gdxArrayOf(
                        Equip(EquipType.SHIP, 1, "DefaultShip", ShipSpecs(
                                gdxFloatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f),
                                gdxFloatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f)
                        )),
                        Equip(EquipType.GUN, 1, "DefaultGun", GunSpecs(
                                gdxFloatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f),
                                gdxFloatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f),
                                gdxFloatArrayOf(3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f, 3.10f),
                                gdxFloatArrayOf(4.1f, 4.2f, 4.3f, 4.4f, 4.5f, 4.6f, 4.7f, 4.8f, 4.9f, 4.10f),
                                gdxFloatArrayOf(5.1f, 5.2f, 5.3f, 5.4f, 5.5f, 5.6f, 5.7f, 5.8f, 5.9f, 5.10f),
                                gdxFloatArrayOf(6.1f, 6.2f, 6.3f, 6.4f, 6.5f, 6.6f, 6.7f, 6.8f, 6.9f, 6.10f)
                        ))
                )
        )
        Assert.assertEquals(outputObj, refObj)
    }
}