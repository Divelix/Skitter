package com.divelix.skitter.unittests.remotedata

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.data.*
import ktx.collections.gdxArrayOf
import ktx.json.fromJson
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PlayerDataTests {
    private lateinit var json: Json
    private lateinit var printSettings: JsonValue.PrettyPrintSettings

    @Before
    fun setup() {
        json = Json()
        printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
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
    fun `check ModAliases serialization`() {
        val inputObj = ModAliases(
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
    fun `check ModAliases deserialization`() {
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
        val outputObj = json.fromJson<ModAliases>(inputStr)
        val refObj = ModAliases(
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

    @Test
    fun `check Equips serialization`() {
        val inputObj = Equips(
                gdxArrayOf(
                        EquipAlias(1, 2, gdxArrayOf(
                                ModAlias(1, 2, 3),
                                ModAlias(4, 5, 6)
                        )),
                        EquipAlias(1, 2, gdxArrayOf(
                                ModAlias(7, 8, 9),
                                ModAlias(10, 11, 12)
                        ))
                ),
                gdxArrayOf(
                        EquipAlias(3, 4, gdxArrayOf(
                                ModAlias(13, 14, 15),
                                ModAlias(16, 17, 18)
                        )),
                        EquipAlias(1, 2, gdxArrayOf(
                                ModAlias(19, 20, 21),
                                ModAlias(22, 23, 24)
                        ))
                )
        )
        val refStr =
                """
                    {
                    "ships": [
                    	{
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "index": 1, "level": 2, "quantity": 3 },
                    			{ "index": 4, "level": 5, "quantity": 6 }
                    		]
                    	},
                    	{
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "index": 7, "level": 8, "quantity": 9 },
                    			{ "index": 10, "level": 11, "quantity": 12 }
                    		]
                    	}
                    ],
                    "guns": [
                    	{
                    		"index": 3,
                    		"level": 4,
                    		"mods": [
                    			{ "index": 13, "level": 14, "quantity": 15 },
                    			{ "index": 16, "level": 17, "quantity": 18 }
                    		]
                    	},
                    	{
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "index": 19, "level": 20, "quantity": 21 },
                    			{ "index": 22, "level": 23, "quantity": 24 }
                    		]
                    	}
                    ]
                    }
                """.trimIndent()
        val outputStr = json.toJson(inputObj)
        val prettyOutputStr = json.prettyPrint(outputStr, printSettings)
        Assert.assertEquals(prettyOutputStr, refStr)
    }

    @Test
    fun `check Equips deserialization`() {
        val inputStr =
                """
                    {
                    "ships": [
                    	{
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "index": 1, "level": 2, "quantity": 3 },
                    			{ "index": 4, "level": 5, "quantity": 6 }
                    		]
                    	},
                    	{
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "index": 7, "level": 8, "quantity": 9 },
                    			{ "index": 10, "level": 11, "quantity": 12 }
                    		]
                    	}
                    ],
                    "guns": [
                    	{
                    		"index": 3,
                    		"level": 4,
                    		"mods": [
                    			{ "index": 13, "level": 14, "quantity": 15 },
                    			{ "index": 16, "level": 17, "quantity": 18 }
                    		]
                    	},
                    	{
                    		"index": 1,
                    		"level": 2,
                    		"mods": [
                    			{ "index": 19, "level": 20, "quantity": 21 },
                    			{ "index": 22, "level": 23, "quantity": 24 }
                    		]
                    	}
                    ]
                    }
                """.trimIndent()
        val outputObj = json.fromJson<Equips>(inputStr)
        val refObj = Equips(
                gdxArrayOf(
                        EquipAlias(1, 2, gdxArrayOf(
                                ModAlias(1, 2, 3),
                                ModAlias(4, 5, 6)
                        )),
                        EquipAlias(1, 2, gdxArrayOf(
                                ModAlias(7, 8, 9),
                                ModAlias(10, 11, 12)
                        ))
                ),
                gdxArrayOf(
                        EquipAlias(3, 4, gdxArrayOf(
                                ModAlias(13, 14, 15),
                                ModAlias(16, 17, 18)
                        )),
                        EquipAlias(1, 2, gdxArrayOf(
                                ModAlias(19, 20, 21),
                                ModAlias(22, 23, 24)
                        ))
                )
        )
        Assert.assertEquals(outputObj, refObj)
    }

    @Test
    fun `check Player serialization`() {
        val inputObj = Player(123, "DefaultName", 100,
                ActiveEquips(
                        ActiveEquip(1, 2, gdxArrayOf(
                                ModAlias(1, 2, 3),
                                ModAlias(4, 5, 6)
                        )),
                        ActiveEquip(2, 3, gdxArrayOf(
                                ModAlias(7, 8, 9),
                                ModAlias(10, 11, 12)
                        ))
                ),
                Equips(
                        gdxArrayOf(
                                EquipAlias(1, 2, gdxArrayOf(
                                        ModAlias(1, 2, 3),
                                        ModAlias(4, 5, 6)
                                )),
                                EquipAlias(1, 2, gdxArrayOf(
                                        ModAlias(7, 8, 9),
                                        ModAlias(10, 11, 12)
                                ))
                        ),
                        gdxArrayOf(
                                EquipAlias(3, 4, gdxArrayOf(
                                        ModAlias(13, 14, 15),
                                        ModAlias(16, 17, 18)
                                )),
                                EquipAlias(1, 2, gdxArrayOf(
                                        ModAlias(19, 20, 21),
                                        ModAlias(22, 23, 24)
                                ))
                        )
                ),
                ModAliases(
                        gdxArrayOf(
                                ModAlias(1, 2, 3),
                                ModAlias(4, 5, 6)
                        ),
                        gdxArrayOf(
                                ModAlias(7, 8, 9),
                                ModAlias(10, 11, 12)
                        )
                )
        )
        val refStr =
                """
                    {
                    "id": 123,
                    "name": "DefaultName",
                    "coins": 100,
                    "activeEquips": {
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
                    },
                    "equips": {
                    	"ships": [
                    		{
                    			"index": 1,
                    			"level": 2,
                    			"mods": [
                    				{ "index": 1, "level": 2, "quantity": 3 },
                    				{ "index": 4, "level": 5, "quantity": 6 }
                    			]
                    		},
                    		{
                    			"index": 1,
                    			"level": 2,
                    			"mods": [
                    				{ "index": 7, "level": 8, "quantity": 9 },
                    				{ "index": 10, "level": 11, "quantity": 12 }
                    			]
                    		}
                    	],
                    	"guns": [
                    		{
                    			"index": 3,
                    			"level": 4,
                    			"mods": [
                    				{ "index": 13, "level": 14, "quantity": 15 },
                    				{ "index": 16, "level": 17, "quantity": 18 }
                    			]
                    		},
                    		{
                    			"index": 1,
                    			"level": 2,
                    			"mods": [
                    				{ "index": 19, "level": 20, "quantity": 21 },
                    				{ "index": 22, "level": 23, "quantity": 24 }
                    			]
                    		}
                    	]
                    },
                    "mods": {
                    	"ship": [
                    		{ "index": 1, "level": 2, "quantity": 3 },
                    		{ "index": 4, "level": 5, "quantity": 6 }
                    	],
                    	"gun": [
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
    fun `check Player deserialization`() {
        val inputStr =
                """
                    {
                    "id": 123,
                    "name": "DefaultName",
                    "coins": 100,
                    "activeEquips": {
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
                    },
                    "equips": {
                    	"ships": [
                    		{
                    			"index": 1,
                    			"level": 2,
                    			"mods": [
                    				{ "index": 1, "level": 2, "quantity": 3 },
                    				{ "index": 4, "level": 5, "quantity": 6 }
                    			]
                    		},
                    		{
                    			"index": 1,
                    			"level": 2,
                    			"mods": [
                    				{ "index": 7, "level": 8, "quantity": 9 },
                    				{ "index": 10, "level": 11, "quantity": 12 }
                    			]
                    		}
                    	],
                    	"guns": [
                    		{
                    			"index": 3,
                    			"level": 4,
                    			"mods": [
                    				{ "index": 13, "level": 14, "quantity": 15 },
                    				{ "index": 16, "level": 17, "quantity": 18 }
                    			]
                    		},
                    		{
                    			"index": 1,
                    			"level": 2,
                    			"mods": [
                    				{ "index": 19, "level": 20, "quantity": 21 },
                    				{ "index": 22, "level": 23, "quantity": 24 }
                    			]
                    		}
                    	]
                    },
                    "mods": {
                    	"ship": [
                    		{ "index": 1, "level": 2, "quantity": 3 },
                    		{ "index": 4, "level": 5, "quantity": 6 }
                    	],
                    	"gun": [
                    		{ "index": 7, "level": 8, "quantity": 9 },
                    		{ "index": 10, "level": 11, "quantity": 12 }
                    	]
                    }
                    }
                """.trimIndent()
        val outputObj = json.fromJson<Player>(inputStr)
        val refObj = Player(123, "DefaultName", 100,
                ActiveEquips(
                        ActiveEquip(1, 2, gdxArrayOf(
                                ModAlias(1, 2, 3),
                                ModAlias(4, 5, 6)
                        )),
                        ActiveEquip(2, 3, gdxArrayOf(
                                ModAlias(7, 8, 9),
                                ModAlias(10, 11, 12)
                        ))
                ),
                Equips(
                        gdxArrayOf(
                                EquipAlias(1, 2, gdxArrayOf(
                                        ModAlias(1, 2, 3),
                                        ModAlias(4, 5, 6)
                                )),
                                EquipAlias(1, 2, gdxArrayOf(
                                        ModAlias(7, 8, 9),
                                        ModAlias(10, 11, 12)
                                ))
                        ),
                        gdxArrayOf(
                                EquipAlias(3, 4, gdxArrayOf(
                                        ModAlias(13, 14, 15),
                                        ModAlias(16, 17, 18)
                                )),
                                EquipAlias(1, 2, gdxArrayOf(
                                        ModAlias(19, 20, 21),
                                        ModAlias(22, 23, 24)
                                ))
                        )
                ),
                ModAliases(
                        gdxArrayOf(
                                ModAlias(1, 2, 3),
                                ModAlias(4, 5, 6)
                        ),
                        gdxArrayOf(
                                ModAlias(7, 8, 9),
                                ModAlias(10, 11, 12)
                        )
                )
        )
        Assert.assertEquals(outputObj, refObj)
    }
}