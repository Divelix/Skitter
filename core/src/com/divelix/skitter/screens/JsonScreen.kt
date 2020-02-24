package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.divelix.skitter.Assets
import com.divelix.skitter.Main
import ktx.app.KtxScreen
import ktx.json.*


class JsonScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()

    init {
        val json = Json()
        testComplex(json)

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
        Gdx.gl.glClearColor(assets.BG_COLOR.r, assets.BG_COLOR.g, assets.BG_COLOR.b, assets.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }
}

private data class Simple(
        var int: Int = 0,
        var bool: Boolean = false,
        var str: String = ""
)

private data class Complex(
        var bool: Boolean = false,
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
        bool = true
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