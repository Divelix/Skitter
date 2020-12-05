package com.divelix.skitter.unittests

import com.badlogic.gdx.utils.FloatArray
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.GunModSerializer
import com.divelix.skitter.GunSerializer
import com.divelix.skitter.ShipModSerializer
import com.divelix.skitter.ShipSerializer
import com.divelix.skitter.data.*
import ktx.collections.GdxFloatArray
import ktx.collections.gdxArrayOf
import ktx.collections.gdxMapOf
import ktx.json.fromJson
import ktx.json.setSerializer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class JsonTests {
    private lateinit var json: Json
    private lateinit var printSettings: JsonValue.PrettyPrintSettings

    @Before
    fun setup() {
        json = Json().apply {
            setUsePrototypes(false) // to not erase default values (false, 0)
            setSerializer(ShipSerializer())
            setSerializer(GunSerializer())
            setSerializer(ShipModSerializer())
            setSerializer(GunModSerializer())
        }
        printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
    }

    @Test
    fun `check Ship serialization`() {
        val inputObj = Ship(1, "DefaultShip", ShipSpecs(
                GdxFloatArray(floatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f)),
                GdxFloatArray(floatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f))
        ))
        val refStr =
                """
                    {
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
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check Ship deserialization`() {
        val inputStr =
                """
                    {
                    "index": 1,
                    "name": "DefaultShip",
                    "specs": {
                    	"health": [ 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 1.1 ],
                    	"speed": [ 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.1 ]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<Ship>(inputStr)
        val refObj = Ship(1, "DefaultShip", ShipSpecs(
                GdxFloatArray(floatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f)),
                GdxFloatArray(floatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f))
        ))
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check Gun serialization`() {
        val inputObj = Gun(1, "DefaultGun", GunSpecs(
                GdxFloatArray(floatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f)),
                GdxFloatArray(floatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f)),
                GdxFloatArray(floatArrayOf(3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f, 3.10f)),
                GdxFloatArray(floatArrayOf(4.1f, 4.2f, 4.3f, 4.4f, 4.5f, 4.6f, 4.7f, 4.8f, 4.9f, 4.10f)),
                GdxFloatArray(floatArrayOf(5.1f, 5.2f, 5.3f, 5.4f, 5.5f, 5.6f, 5.7f, 5.8f, 5.9f, 5.10f)),
                GdxFloatArray(floatArrayOf(6.1f, 6.2f, 6.3f, 6.4f, 6.5f, 6.6f, 6.7f, 6.8f, 6.9f, 6.10f))
        ))
        val refStr =
                """
                    {
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
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check Gun deserialization`() {
        val inputStr =
                """
                    {
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
                """.trimIndent()
        val outputObj = json.fromJson<Gun>(inputStr)
        val refObj = Gun(1, "DefaultGun", GunSpecs(
                GdxFloatArray(floatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f)),
                GdxFloatArray(floatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f)),
                GdxFloatArray(floatArrayOf(3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f, 3.10f)),
                GdxFloatArray(floatArrayOf(4.1f, 4.2f, 4.3f, 4.4f, 4.5f, 4.6f, 4.7f, 4.8f, 4.9f, 4.10f)),
                GdxFloatArray(floatArrayOf(5.1f, 5.2f, 5.3f, 5.4f, 5.5f, 5.6f, 5.7f, 5.8f, 5.9f, 5.10f)),
                GdxFloatArray(floatArrayOf(6.1f, 6.2f, 6.3f, 6.4f, 6.5f, 6.6f, 6.7f, 6.8f, 6.9f, 6.10f))
        ))
        Assert.assertEquals(outputObj, refObj)
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
    fun `check ModAlias serialization`() {
        val inputObj = ModAlias(1, 2, 3)
        val refStr = """{ "index": 1, "level": 2, "quantity": 3 }"""
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check ModAlias deserialization`() {
        val inputStr = """{ "index": 1, "level": 2, "quantity": 3 }"""
        val outputObj = json.fromJson<ModAlias>(inputStr)
        val refObj = ModAlias(1, 2, 3)
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check ActiveEquip serialization`() {
        val inputObj = ActiveEquip(1, 2, gdxArrayOf(
                ModAlias(1, 2, 3),
                ModAlias(4, 5, 6)
        ))
        val refStr =
                """
                    {
                    "index": 1,
                    "level": 2,
                    "mods": [
                    	{ "index": 1, "level": 2, "quantity": 3 },
                    	{ "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check ActiveEquip deserialization`() {
        val inputStr =
                """
                    {
                    "index": 1,
                    "level": 2,
                    "mods": [
                    	{ "index": 1, "level": 2, "quantity": 3 },
                    	{ "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<ActiveEquip>(inputStr)
        val refObj = ActiveEquip(1, 2, gdxArrayOf(
                ModAlias(1, 2, 3),
                ModAlias(4, 5, 6)
        ))
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check ActiveEquips serialization`() {
        val inputObj = ActiveEquips(
                ActiveEquip(1, 2, gdxArrayOf(
                        ModAlias(1, 2, 3),
                        ModAlias(4, 5, 6)
                )),
                ActiveEquip(2, 3, gdxArrayOf(
                        ModAlias(7, 8, 9),
                        ModAlias(10, 11, 12)
                ))
        )
        val refStr =
                """
                    {
                    "ship": {
                    	"index": 1,
                    	"level": 2,
                    	"mods": [
                    		{ "index": 1, "level": 2, "quantity": 3 },
                    		{ "index": 4, "level": 5, "quantity": 6 }
                    	]
                    },
                    "gun": {
                    	"index": 2,
                    	"level": 3,
                    	"mods": [
                    		{ "index": 7, "level": 8, "quantity": 9 },
                    		{ "index": 10, "level": 11, "quantity": 12 }
                    	]
                    }
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check ActiveEquips deserialization`() {
        val inputStr =
                """
                    {
                    "ship": {
                    	"index": 1,
                    	"level": 2,
                    	"mods": [
                    		{ "index": 1, "level": 2, "quantity": 3 },
                    		{ "index": 4, "level": 5, "quantity": 6 }
                    	]
                    },
                    "gun": {
                    	"index": 2,
                    	"level": 3,
                    	"mods": [
                    		{ "index": 7, "level": 8, "quantity": 9 },
                    		{ "index": 10, "level": 11, "quantity": 12 }
                    	]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<ActiveEquips>(inputStr)
        val refObj = ActiveEquips(
                ActiveEquip(1, 2, gdxArrayOf(
                        ModAlias(1, 2, 3),
                        ModAlias(4, 5, 6)
                )),
                ActiveEquip(2, 3, gdxArrayOf(
                        ModAlias(7, 8, 9),
                        ModAlias(10, 11, 12)
                ))
        )
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check Mods serialization`() {
        val inputObj = Mods(
            gdxArrayOf(
                ModAlias(1, 2, 3),
                ModAlias(4, 5, 6)
            ),
            gdxArrayOf(
                ModAlias(7, 8, 9),
                ModAlias(10, 11, 12)
            )
        )
        val refStr =
                """
                    {
                    "ship": [
                    	{ "index": 1, "level": 2, "quantity": 3 },
                    	{ "index": 4, "level": 5, "quantity": 6 }
                    ],
                    "gun": [
                    	{ "index": 7, "level": 8, "quantity": 9 },
                    	{ "index": 10, "level": 11, "quantity": 12 }
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
                    	{ "index": 1, "level": 2, "quantity": 3 },
                    	{ "index": 4, "level": 5, "quantity": 6 }
                    ],
                    "gun": [
                    	{ "index": 7, "level": 8, "quantity": 9 },
                    	{ "index": 10, "level": 11, "quantity": 12 }
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<Mods>(inputStr)
        val refObj = Mods(
            gdxArrayOf(
                ModAlias(1, 2, 3),
                ModAlias(4, 5, 6)
            ),
            gdxArrayOf(
                ModAlias(7, 8, 9),
                ModAlias(10, 11, 12)
            )
        )
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check EquipAlias serialization`() {
        val inputObj = EquipAlias(1, 2, gdxArrayOf(
            ModAlias(1, 2, 3),
            ModAlias(4, 5, 6)
        ))
        val refStr =
                """
                    {
                    "index": 1,
                    "level": 2,
                    "mods": [
                    	{ "index": 1, "level": 2, "quantity": 3 },
                    	{ "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check EquipAlias deserialization`() {
        val inputStr =
                """
                    {
                    "index": 1,
                    "level": 2,
                    "mods": [
                    	{ "index": 1, "level": 2, "quantity": 3 },
                    	{ "index": 4, "level": 5, "quantity": 6 }
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<EquipAlias>(inputStr)
        val refObj = EquipAlias(1, 2, gdxArrayOf(
                ModAlias(1, 2, 3),
                ModAlias(4, 5, 6)
        ))
        Assert.assertEquals(outputObj, refObj)
    }
}