package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.utils.TopViewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.log.debug
import ktx.vis.table
import ktx.vis.window

class TestUIScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    init {
        val bgPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(Color(0f, 0f, 0f, 0.5f)); fill()}))
        val root = table {
            setFillParent(true)
            top()
            background = bgDrawable
            textButton("left")
            textButton("right")
        }
        val rows = listOf(
                Pair(assets.manager.get<Texture>(Constants.WOMB), 12),
                Pair(assets.manager.get<Texture>(Constants.RADIAL), 6),
                Pair(assets.manager.get<Texture>(Constants.JUMPER), 10)
        )
        stage += root
        stage += window("Game Over") {
            debugAll()
            centerWindow()
            padTop(50f) // title height
            defaults().top()
            width = 320f
            height = 500f
            row()
            // Stats table
            table {
                val iconWidth = 50f
                val cellWidth = 150f
                debug = true
                padTop(25f)
                defaults().padTop(10f)
                rows.forEach {
                    val ratio = it.first.width.toFloat() / it.first.height
                    image(TextureRegionDrawable(it.first)).cell(width = iconWidth, height = iconWidth / ratio)
                    label("x${it.second}")
                    label("${it.second * 10}").cell(width = cellWidth).setAlignment(Align.center)
                    row()
                }
            }.cell(colspan = 2, expand = true)
            row()
            imageButton("restart").addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    debug(TAG) {"Restart match"}
                }
            })
            imageButton("home").addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    debug(TAG) {"Go to menu"}
                }
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