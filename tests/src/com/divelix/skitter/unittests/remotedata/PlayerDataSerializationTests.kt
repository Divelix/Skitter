package com.divelix.skitter.unittests.remotedata

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.EquipAliasSerializer
import com.divelix.skitter.ModAliasSerializer
import com.divelix.skitter.data.*
import ktx.collections.gdxArrayOf
import ktx.json.fromJson
import ktx.json.setSerializer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PlayerDataSerializationTests {
    private lateinit var json: Json
    private lateinit var printSettings: JsonValue.PrettyPrintSettings

    @Before
    fun setup() {
        json = Json().apply {
            setSerializer(EquipAliasSerializer())
            setSerializer(ModAliasSerializer())

        }
        printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
    }

    @Test
    fun `check ModAlias serialization`() {
        val inputObj = ModAlias(ModType.SHIP_MOD, 1, 2, 3)
        val refStr = """{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 }"""
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(refStr, prettyOutputStr)
    }

    @Test
    fun `check ModAlias deserialization`() {
        val inputStr = """{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 }"""
        val outputObj = json.fromJson<ModAlias>(inputStr)
        val refObj = ModAlias(ModType.SHIP_MOD, 1, 2, 3)
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check EquipAlias serialization`() {
        val inputObj = EquipAlias(EquipType.SHIP, 1, 2, gdxArrayOf(
                ModAlias(ModType.SHIP_MOD, 1, 2, 3),
                ModAlias(ModType.SHIP_MOD, 4, 5, 6)
        ))
        val refStr =
                """
                    {
                    "type": "ship",
                    "index": 1,
                    "level": 2,
                    "mods": [
                    	{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 },
                    	{ "type": "ship_mod", "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(refStr, prettyOutputStr)
    }

    @Test
    fun `check EquipAlias deserialization`() {
        val inputStr =
                """
                    {
                    "type": "ship",
                    "index": 1,
                    "level": 2,
                    "mods": [
                    	{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 },
                    	{ "type": "ship_mod", "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<EquipAlias>(inputStr)
        val refObj = EquipAlias(EquipType.SHIP, 1, 2, gdxArrayOf(
                ModAlias(ModType.SHIP_MOD, 1, 2, 3),
                ModAlias(ModType.SHIP_MOD, 4, 5, 6)
        ))
        Assert.assertEquals(refObj, outputObj)
    }

    @Test
    fun `check ActiveEquips serialization`() {
        val inputObj = ActiveEquips(2, 5)
        val refStr = """{ "shipIndex": 2, "gunIndex": 5 }"""
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(refStr, prettyOutputStr)
    }

    @Test
    fun `check ActiveEquips deserialization`() {
        val inputStr = """{ "shipIndex": 2, "gunIndex": 5 }"""
        val outputObj = json.fromJson<ActiveEquips>(inputStr)
        val refObj = ActiveEquips(2, 5)
        Assert.assertEquals(refObj, outputObj)
    }

    @Test
    fun `check Player serialization`() {
        val inputObj = PlayerData(123, "DefaultName", 100,
                ActiveEquips(1, 1),
                gdxArrayOf(
                        EquipAlias(EquipType.SHIP, 1, 2, gdxArrayOf(
                                ModAlias(ModType.SHIP_MOD, 1, 2, 3),
                                ModAlias(ModType.SHIP_MOD, 4, 5, 6)
                        )),
                        EquipAlias(EquipType.SHIP, 1, 2, gdxArrayOf(
                                ModAlias(ModType.SHIP_MOD, 7, 8, 9),
                                ModAlias(ModType.SHIP_MOD, 10, 11, 12)
                        ))
                ),
                gdxArrayOf(
                        ModAlias(ModType.SHIP_MOD, 1, 2, 3),
                        ModAlias(ModType.SHIP_MOD, 4, 5, 6)
                )
        )
        val refStr =
                """
                    {
                    "id": 123,
                    "name": "DefaultName",
                    "coins": 100,
                    "activeEquips": { "shipIndex": 1, "gunIndex": 1 },
                    "equips": [
                    	{
                    		"type": "ship",
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 },
                    			{ "type": "ship_mod", "index": 4, "level": 5, "quantity": 6 }
                    		]
                    	},
                    	{
                    		"type": "ship",
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "type": "ship_mod", "index": 7, "level": 8, "quantity": 9 },
                    			{ "type": "ship_mod", "index": 10, "level": 11, "quantity": 12 }
                    		]
                    	}
                    ],
                    "mods": [
                    	{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 },
                    	{ "type": "ship_mod", "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(refStr, prettyOutputStr)
    }

    @Test
    fun `check Player deserialization`() {
        val inputStr =
                """
                    {
                    "id": 123,
                    "name": "DefaultName",
                    "coins": 100,
                    "activeEquips": { "shipIndex": 1, "gunIndex": 1 },
                    "equips": [
                    	{
                    		"type": "ship",
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 },
                    			{ "type": "ship_mod", "index": 4, "level": 5, "quantity": 6 }
                    		]
                    	},
                    	{
                    		"type": "ship",
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "type": "ship_mod", "index": 7, "level": 8, "quantity": 9 },
                    			{ "type": "ship_mod", "index": 10, "level": 11, "quantity": 12 }
                    		]
                    	}
                    ],
                    "mods": [
                    	{ "type": "ship_mod", "index": 1, "level": 2, "quantity": 3 },
                    	{ "type": "ship_mod", "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<PlayerData>(inputStr)
        val refObj = PlayerData(123, "DefaultName", 100,
                ActiveEquips(1, 1),
                gdxArrayOf(
                        EquipAlias(EquipType.SHIP, 1, 2, gdxArrayOf(
                                ModAlias(ModType.SHIP_MOD, 1, 2, 3),
                                ModAlias(ModType.SHIP_MOD, 4, 5, 6)
                        )),
                        EquipAlias(EquipType.SHIP, 1, 2, gdxArrayOf(
                                ModAlias(ModType.SHIP_MOD, 7, 8, 9),
                                ModAlias(ModType.SHIP_MOD, 10, 11, 12)
                        ))
                ),
                gdxArrayOf(
                        ModAlias(ModType.SHIP_MOD, 1, 2, 3),
                        ModAlias(ModType.SHIP_MOD, 4, 5, 6)
                )
        )
        Assert.assertEquals(outputObj, refObj)
    }
}