package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.Noise
import ktx.app.KtxScreen
import ktx.graphics.*

class FancyScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val shape = context.inject<ShapeRenderer>()
    private val assets = context.inject<Assets>()

    private val skinGradTexture = assets.manager.get<Texture>(Constants.SKIN_GRAD)
    private val skinGradReg = TextureRegion(skinGradTexture)

    private val pixmap = Pixmap(100, 100, Pixmap.Format.Intensity)
    private val noise = Noise(1)

    init {
        skinGradTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge)
        skinGradReg.setRegion(0, 0, Constants.D_WIDTH, Constants.D_HEIGHT)
        println("Noise value: ${noise.getConfiguredNoise(10f, 10f)}")
    }
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.use {
            batch.draw(skinGradTexture, 0f, 0f, Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat())
            batch.draw(Texture(pixmap), 0f, 0f)
        }

        Gdx.gl.glEnable(GL20.GL_BLEND)
        shape.use(ShapeRenderer.ShapeType.Filled) {
            shape.setColor(1f, 1f, 1f,0.5f)
            shape.circle(Vector2(100f, 100f), 10f)
        }
//        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
}