package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.Main
import ktx.app.KtxScreen
import ktx.assets.file
import ktx.graphics.*

class ParticleScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    val camera = OrthographicCamera()
    val pos = Vector3()
    val effectPool: ParticleEffectPool
    val effects = Array<ParticleEffectPool.PooledEffect>()
    val myEffect = ParticleEffect()

    init {
        myEffect.load(file("effects/gas-burner.p"), file("effects"))
//        myEffect.scaleEffect(0.1f)
        effectPool = ParticleEffectPool(myEffect, 5, 10)

        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.R -> refresh()
                    Input.Keys.G -> generate(10)
                    Input.Keys.P -> println(effects.size)
                }
                return super.keyDown(keycode)
            }
        }
        Gdx.input.inputProcessor = handler
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.BG_COLOR.r, assets.BG_COLOR.g, assets.BG_COLOR.b, assets.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        pos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(pos)
        batch.use {
            for (effect in effects) {
//                e.setPosition(pos.x, pos.y)
                effect.draw(it, delta)
                if (effect.isComplete) {
                    effect.free()
                    effects.removeValue(effect, false)
                }
            }
        }
//        println("FPS: ${Gdx.graphics.framesPerSecond}; effects: ${effects.size}")
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    fun refresh() {
        effects.forEach {it.free()}
        effects.clear()
    }

    fun generate(count: Int) {
        for (i in 1..count) {
            effects.add(effectPool.obtain().apply {
                setPosition(MathUtils.random(0f, 350f), MathUtils.random(0f, 500f))
            })
        }
    }

    override fun dispose() {
        myEffect.dispose()
    }
}