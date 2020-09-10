package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.*
import com.divelix.skitter.Main
import com.divelix.skitter.data.*
import com.divelix.skitter.ui.scrollmenu.ScrollMenu
import com.divelix.skitter.utils.TopViewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.toLocalFile
import ktx.collections.*
import ktx.graphics.use

class ScrollMenuScreen(game: Main) : KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    private val aspectRatio = Gdx.graphics.height.toFloat() / Gdx.graphics.width
    private val stage = Stage(TopViewport(Constants.STAGE_WIDTH.toFloat(), Constants.STAGE_WIDTH * aspectRatio), batch)

    val scrollMenu: ScrollMenu

    init {
        scrollMenu = ScrollMenu(context)
        stage += scrollMenu
        stage.isDebugAll = true
        val handler = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.NUM_1 -> scrollMenu.scrollPane.scrollX = 0f
                    Input.Keys.NUM_2 -> scrollMenu.scrollPane.scrollX = 350f
                    Input.Keys.NUM_3 -> scrollMenu.scrollPane.scrollX = 700f
                    Input.Keys.S -> scrollMenu.saveToJson()
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(stage, handler)
        Gdx.input.inputProcessor = multiplexer
    }

    // It make new instance of PlayerData and write it to json file
    fun fillJsonFile() {
        val file = "json/playerData.json".toLocalFile()
        val printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }

        val playerData = PlayerData(
                id = 1234,
                name = "Lex",
                coins = 525,
                activeShip = 1,
                activeShipSpecs = gdxArrayOf(100f, 20f),
                activeGun = 2,
                activeGunSpecs = gdxArrayOf(30f, 10f, 1f, 25f, 2f, 0.1f),
                ships = gdxArrayOf(EquipData(
                        index = 1,
                        level = 1,
                        mods = gdxArrayOf(
                                ModAvatarData(index = 1001, level = 1)
                        )
                )),
                guns = gdxArrayOf(EquipData(
                        index = 1,
                        level = 1,
                        mods = gdxArrayOf()
                )),
                mods = PlayerModsData(
                        ship = gdxArrayOf(
                                ModAvatarData(index = 1001, level = 1, quantity = 1),
                                ModAvatarData(index = 1002, level = 2, quantity = 2),
                                ModAvatarData(index = 1003, level = 1, quantity = 3)
                        ),
                        gun = gdxArrayOf(
                                ModAvatarData(index = 2001, level = 1, quantity = 1),
                                ModAvatarData(index = 2002, level = 1, quantity = 2),
                                ModAvatarData(index = 2002, level = 1, quantity = 3),
                                ModAvatarData(index = 2002, level = 1, quantity = 4),
                                ModAvatarData(index = 2002, level = 1, quantity = 5),
                                ModAvatarData(index = 2002, level = 1, quantity = 6)
                        )
                )
        )
        val json = context.inject<Json>()
        file.writeString(json.prettyPrint(playerData, printSettings), false)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.BG_COLOR.r, assets.BG_COLOR.g, assets.BG_COLOR.b, assets.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}