package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.menu.tabs.Tab
import com.divelix.skitter.ui.menu.tabs.TabbedMenu
import com.divelix.skitter.utils.TopViewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.collections.gdxArrayOf

class TestUIScreen(val game: Main) : KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.STAGE_WIDTH.toFloat(), Constants.stageHeight), batch)

    init {
        stage.isDebugAll = true
        stage += scene2d.table {
            setFillParent(true)
            top()

            val tabbedMenu = TabbedMenu(gdxArrayOf(
//                    Tab(assets.manager.get<Texture>(Constants.SHIP_ICON), ShipTable(assets)),
                    Tab(assets.manager.get<Texture>(Constants.GUN_ICON), scene2d.table { label("second") }),
                    Tab(assets.manager.get<Texture>(Constants.MOD_GUN_CAPACITY), scene2d.table { label("third") })
            ))
            add(tabbedMenu)
        }
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, false)
    }

    companion object {
        private val TAG = TestUIScreen::class.simpleName!!
    }
}

