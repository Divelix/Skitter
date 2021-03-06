package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.divelix.skitter.Main
import com.divelix.skitter.data.Assets
import ktx.app.KtxScreen
import ktx.graphics.use

class LoadingScreen(private val game: Main): KtxScreen {

    private var progress = 0f
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val shape = context.inject<ShapeRenderer>()
    private val assets = context.inject<Assets>()

    init {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        assets.loadSplashAssets()
        assets.loadAssets()
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height) // lwjgl3 need it
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        progress = MathUtils.lerp(progress, assets.manager.progress, 0.1f)

        shape.use(ShapeRenderer.ShapeType.Filled) {
            shape.color = assets.bgColor
            shape.rect(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height*progress)
        }
        batch.use {
            assets.digitsFont.draw(batch, "${(progress*100).toInt()}%", Gdx.graphics.width / 2f - 50f, Gdx.graphics.height / 2f)
        }

        if(assets.manager.update() && progress >= 0.99f) {
            assets.manager.finishLoading()
            assets.setup()
            game.screen = ScrollMenuScreen(game)
        }
    }
}