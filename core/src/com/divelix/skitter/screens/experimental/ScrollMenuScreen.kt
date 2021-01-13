package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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

class ScrollMenuScreen(game: Main) : KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.STAGE_WIDTH.toFloat(), Constants.stageHeight), batch)

    val scrollMenu: ScrollMenu

    init {
//        fillJsonFile()
        scrollMenu = ScrollMenu(context)
        stage += scrollMenu
//        stage.isDebugAll = true
        val handler = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
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

        val playerData = Player(123, "DefaultName", 100,
                ActiveEquips(
                        EquipAlias(EquipType.SHIP, 1, 2, gdxArrayOf(
                                ModAlias(ModType.SHIP_MOD, 1, 2, 3),
                                ModAlias(ModType.SHIP_MOD, 4, 5, 6)
                        )),
                        EquipAlias(EquipType.GUN, 1, 2, gdxArrayOf(
                                ModAlias(ModType.GUN_MOD, 1, 2, 3),
                                ModAlias(ModType.GUN_MOD, 4, 5, 6)
                        ))
                ),
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
                        ModAlias(ModType.GUN_MOD, 4, 5, 6)
                )
        )
        val json = context.inject<Json>()
        file.writeString(json.prettyPrint(playerData, printSettings), false)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.bgColor.r, assets.bgColor.g, assets.bgColor.b, assets.bgColor.a)
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