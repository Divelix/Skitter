package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.divelix.skitter.Assets
import com.divelix.skitter.Main
import com.divelix.skitter.Constants
import ktx.app.KtxScreen

class LoadingScreen(private val game: Main): KtxScreen {

    private var progress = 0f
    private val greenSquare: Texture
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()

    init {
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
        assets.loadSplashAssets()
        greenSquare = assets.manager.get(Constants.LOADING_IMAGE)
        assets.loadAssets()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0.1f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        progress = MathUtils.lerp(progress, assets.manager.progress, 0.1f)

        batch.begin()
        batch.draw(greenSquare, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height*progress)
        assets.digitsFont.draw(batch, "${(progress*100).toInt()}%", Gdx.graphics.width / 2f - 50f, Gdx.graphics.height / 2f)
        batch.end()

        if(assets.manager.update() && progress >= 0.99f) {
            assets.manager.finishLoading()
            assets.setup()
            game.screen = MenuScreen(game)
        }
    }
}