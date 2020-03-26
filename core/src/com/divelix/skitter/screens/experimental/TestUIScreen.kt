package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.Mod
import com.divelix.skitter.utils.TopViewport
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.actors.plusAssign
import ktx.app.KtxScreen
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
        stage += root
        stage += window("jopa") {
            centerWindow()
            padTop(25f)
            width = 300f
            height = 400f
//            label("WhobaW")
//            row()
            for (i in 1..49) {
                textButton("    ")
                if (i % 7 == 0) row()
            }
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
}