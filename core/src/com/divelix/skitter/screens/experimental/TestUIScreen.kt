package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.ImgBgButton
import com.divelix.skitter.utils.TopViewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visTable
import ktx.scene2d.vis.visTextButton
import ktx.scene2d.vis.visWindow

class TestUIScreen(val game: Main) : KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    init {
        val bgPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 0.5f)); fill() }))
        val root = scene2d.visTable {
            setFillParent(true)
            top()
            background = bgDrawable
            visTextButton("left")
            visTextButton("right")
        }
        val rows = listOf(
                Pair(assets.manager.get<Texture>(Constants.WOMB), 12),
                Pair(assets.manager.get<Texture>(Constants.RADIAL), 6),
                Pair(assets.manager.get<Texture>(Constants.JUMPER), 10)
        )
        stage += root
        stage += scene2d.visWindow("Game Over") {
            debugAll()
            centerWindow()
            padTop(50f) // title height
            defaults().top()
            width = 320f
            height = 500f
            row()
            // Stats table
            visTable {
                val iconWidth = 50f
                val cellWidth = 150f
                debug = true
                padTop(25f)
                defaults().padTop(10f)
                rows.forEach {
                    val ratio = it.first.width.toFloat() / it.first.height
                    visImage(TextureRegionDrawable(it.first)).cell(width = iconWidth, height = iconWidth / ratio)
                    label("x${it.second}")
                    label("${it.second * 10}").cell(width = cellWidth).setAlignment(Align.center)
                    row()
                }
            }.cell(colspan = 2, expand = true)
            row()
            add(ImgBgButton(assets, assets.manager.get<Texture>(Constants.RESTART_ICON)) {
                println("Restart")
            })
            add(ImgBgButton(assets, assets.manager.get<Texture>(Constants.HOME_ICON)) {
                println("Home")
            })
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