package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.utils.TopViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisScrollPane
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.app.KtxScreen
import ktx.vis.table

class SwipeMenuScreen(game: Main): KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    val aspectRatio = Gdx.graphics.height.toFloat() / Gdx.graphics.width
    val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_WIDTH * aspectRatio), batch)

    lateinit var infoLabel: Label
    lateinit var  scrollP: VisScrollPane

    init {
        val bigTable = table {
            width = Gdx.graphics.width * 3f
            height = Gdx.graphics.height.toFloat()
            table {
                add(Image(assets.manager.get<Texture>(Constants.BACKGROUND_IMAGE))).width(Gdx.graphics.width.toFloat()).height(Gdx.graphics.height.toFloat())
            }
            table {
                add(Image(assets.manager.get<Texture>(Constants.SELL_BTN))).width(Gdx.graphics.width.toFloat()).height(Gdx.graphics.height.toFloat())
            }
            table {
                add(Image(assets.manager.get<Texture>(Constants.UP_BTN))).width(Gdx.graphics.width.toFloat()).height(Gdx.graphics.height.toFloat())
            }
        }
        val rootTable = table {
            setFillParent(true)
            scrollP = scrollPane(bigTable) { setScrollingDisabled(false, true) }
        }
        infoLabel = Label("Scroll: ${scrollP.scrollX}", VisUI.getSkin())
        stage += rootTable
        stage += infoLabel
        stage.isDebugAll = true
        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.NUM_1 -> scrollP.scrollX = 0f
                    Input.Keys.NUM_2 -> scrollP.scrollX = 350f
                    Input.Keys.NUM_3 -> scrollP.scrollX = 700f
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(stage, handler)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.BG_COLOR.r, assets.BG_COLOR.g, assets.BG_COLOR.b, assets.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        infoLabel.txt = "Scroll: ${scrollP.scrollX}"
        infoLabel.pack()
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }
}