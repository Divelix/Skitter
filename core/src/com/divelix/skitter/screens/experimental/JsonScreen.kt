package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.FloatArray
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.Main
import com.divelix.skitter.data.*
import com.divelix.skitter.screens.MenuScreen
import ktx.app.KtxScreen
import ktx.assets.toLocalFile
import ktx.collections.*
import ktx.json.*

class JsonScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    init {
        val json = Json().apply {
            setUsePrototypes(false) // to not erase default values (false, 0)
//            setSerializer(Vector2AsArraySerializer())
//            setSerializer(Vector3AsArraySerializer())
            setSerializer(ShipModSerializer())
        }
//        testComplex(json)
//        testChapter(json)
        testNewData(json)

        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK, Input.Keys.ESCAPE  -> game.screen = MenuScreen(game)
                }
                return true
            }
        }
        Gdx.input.inputProcessor = handler
        Gdx.app.exit()
    }
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.bgColor.r, assets.bgColor.g, assets.bgColor.b, assets.bgColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }
}

private enum class Names { SEREGA, SVETA, MASHA }

private data class Simple(
        var int: Int = 0,
        var bool: Boolean = false,
        var str: String = ""
)

private data class Complex(
        var bool: Boolean = false,
        var name: Names = Names.SEREGA,
        var vec2: Vector2 = Vector2(),
        var vec3: Vector3 = Vector3(),
        var simple: Simple = Simple(),
        var list: Array<Int> = Array(),
        var sList: List<Simple> = emptyList()
)

private data class Custom(
        var float: Float = 0f,
        var simple: Simple = Simple(),
        var list: List<Simple> = emptyList()) : Json.Serializable {

    override fun read(json: Json, jsonData: JsonValue) {
        float = json.readValue(jsonData, "float")
        simple = json.readValue(jsonData, "simple")
        list = json.readArrayValue(jsonData, "list")
    }

    override fun write(json: Json) {
        json.writeValue("float", float)
        json.writeValue("simple", simple)
        json.writeValue("list", list)
    }
}

fun testComplex(json: Json) {
    var complexStr = """{
      "bool": true,
      "simple": {
        "int": 31,
        "bool": true,
        "str": "abracadabra"
      },
      "list": [1, 1, 2, 3, 5, 8, 13],
      "sList": [
      {
      "int": 1,
      "bool": true,
      "str": "lalala"
      },
      {
      "int": 2,
      "bool": true,
      "str": "azaza"
      }
      ]
    }"""
    var complex = json.fromJson<Complex>(complexStr)
    complex = Complex().apply {
        bool = false
        name = Names.MASHA
        vec2 = Vector2(2f, 4f)
        vec3 = Vector3(3f, 9f, 15f)
        simple = Simple().apply {
            int = 123
            bool = true
            str = "simpl"
        }
        list = Array()
        list.add(11)
        list.add(99)
        sList = mutableListOf(Simple(1, true, "s1"), Simple(2, true, "s2"), Simple(3, true, "s3"))
    }
    complexStr = json.toJson(complex)
    complex = json.fromJson<Complex>(complexStr)
    complexStr = json.toJson(complex)
    complex = json.fromJson<Complex>(complexStr)
    complexStr = json.toJson(complex)
    println(json.prettyPrint(complexStr))
}

fun testChapter(json: Json) {
    val file = "json/chapters.json".toLocalFile()
    val printSettings = JsonValue.PrettyPrintSettings().apply {
        outputType = JsonWriter.OutputType.json
        singleLineColumns = 100
    }
    var chapter = Chapter(
            "First Chapter",
            gdxArrayOf(
                    Level(
                            Vector2(15f, 30f),
                            gdxArrayOf(
                                    EnemyBundle(Enemy.SNIPER, 2),
                                    EnemyBundle(Enemy.RADIAL, 3)
                            )
                    ),
                    Level(
                            Vector2(15f, 20f),
                            gdxArrayOf(
                                    EnemyBundle(Enemy.SNIPER, 1),
                                    EnemyBundle(Enemy.RADIAL, 1)
                            )
                    )
            )
    )
    var chapterStr = json.toJson(chapter)
//    chapter = json.fromJson(chapterStr)
//    chapterStr = json.toJson(chapter)
    file.writeString(json.prettyPrint(chapterStr, printSettings), false)
//    println(json.prettyPrint(chapterStr))
}

fun testNewData(json: Json) {
    val ship = Ship(1, "DefaultShip", ShipSpecs(
            gdxArrayOf(1f, 2f, 3f, 4f, 5f),
            gdxArrayOf(1.1f, 2.2f, 3.3f, 4.4f, 5.5f)
    ))
    val shipStr = json.toJson(ship)
    println(json.prettyPrint(shipStr))

//    json.setElementType<ShipMod, FloatArray>("effects")
    val fa = FloatArray(2)
    fa.add(1f)
    fa.add(2f)
    val modHpBooster = ShipMod(13, "MODNAME", gdxMapOf(
            "http" to fa
    ))
    val modStr = json.toJson(modHpBooster)
    println(json.prettyPrint(modStr))
//    val modStr2 = "{ index: 1, name: modName, effects: { hp: [1.0, 2.0], speed: [3, 4]}}"
    val newMod = json.fromJson<ShipMod>(modStr)
    println(newMod)
}

class ShipModSerializer: JsonSerializer<ShipMod> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): ShipMod {
        val index = jsonValue[0].asInt()
        val name = jsonValue[1].asString()
        val effects = gdxMapOf<String, FloatArray>()
        jsonValue[2].forEach {
            effects.put(it.name, it.asFloatArray().toGdxArray())
        }
        return ShipMod(index, name, effects).also { println(it) }
    }

    override fun write(json: Json, value: ShipMod, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue(ShipMod::index.name, value.index)
            writeValue(ShipMod::name.name, value.name)
            writeObjectStart("effects")
            value.effects.forEach {
                json.writeValue(it.key, it.value.toArray())
            }
            writeObjectEnd()
            writeObjectEnd()
        }
    }
}