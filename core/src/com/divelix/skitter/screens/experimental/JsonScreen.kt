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
import com.divelix.skitter.*
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
            setSerializer(ShipSerializer())
            setSerializer(GunSerializer())
            setSerializer(ShipModSerializer())
            setSerializer(GunModSerializer())
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

fun testNewData(json: Json) {
    println("---- test Ship ----")
    val ship = Ship(1, "DefaultShip", ShipSpecs(
            GdxFloatArray(floatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f)),
            GdxFloatArray(floatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f))
    ))
    val shipStr = json.toJson(ship)
    println(json.prettyPrint(shipStr))
    val newShip = json.fromJson<Ship>(shipStr)
    println(newShip)

    println("---- test Gun ----")
    val gun = Gun(1, "DefaultGun", GunSpecs(
            GdxFloatArray(floatArrayOf(1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 1.10f)),
            GdxFloatArray(floatArrayOf(2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 2.10f)),
            GdxFloatArray(floatArrayOf(3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f, 3.10f)),
            GdxFloatArray(floatArrayOf(4.1f, 4.2f, 4.3f, 4.4f, 4.5f, 4.6f, 4.7f, 4.8f, 4.9f, 4.10f)),
            GdxFloatArray(floatArrayOf(5.1f, 5.2f, 5.3f, 5.4f, 5.5f, 5.6f, 5.7f, 5.8f, 5.9f, 5.10f)),
            GdxFloatArray(floatArrayOf(6.1f, 6.2f, 6.3f, 6.4f, 6.5f, 6.6f, 6.7f, 6.8f, 6.9f, 6.10f))
    ))
    val gunStr = json.toJson(gun)
    println(json.prettyPrint(gunStr))
    val newGun = json.fromJson<Gun>(gunStr)
    println(newGun)

    println("---- test ShipMod ----")
    val fa = FloatArray(floatArrayOf(1f, 2f))
    val modHpBooster = ShipMod(13, "SHIP_MOD_NAME", gdxMapOf(
            ShipModEffects.HealthBooster to fa
    ))
    val modStr = json.toJson(modHpBooster)
    println(json.prettyPrint(modStr))
    val newMod = json.fromJson<ShipMod>(modStr)
    println(newMod)

    println("---- test GunMod ----")
    val fa2 = FloatArray(floatArrayOf(1f, 2f))
    val gunMod = GunMod(15, "GUN_MOD_NAME", gdxMapOf(
            GunModEffects.DamageBooster to fa2
    ))
    val gunModStr = json.toJson(gunMod)
    println(json.prettyPrint(gunModStr))
    val newGunMod = json.fromJson<GunMod>(gunModStr)
    println(newGunMod)
}

