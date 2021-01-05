package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.Data
import com.divelix.skitter.Main
import com.divelix.skitter.data.Drawables
import ktx.app.KtxScreen
import ktx.assets.file
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import ktx.style.get

class DistortionScreen(game: Main): KtxScreen {
    var isPause = false
    val scale = 1f / 32f // pretend it is in box2d
    val shift = 10f * scale
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    val camera = OrthographicCamera()

    private val shader = ShaderProgram(file(Constants.VERTEX_SHADER), file(Constants.FRAGMENT_SHADER))
    private val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)
    private var bufferTexture = TextureRegion(frameBuffer.colorBufferTexture)

    init {
        ShaderProgram.pedantic = false
        println(if(shader.isCompiled) "shader successfully compiled" else shader.log)
        shader.use {
            shader.setUniformf("u_resolution", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            println("${Gdx.graphics.width.toFloat()}, ${Gdx.graphics.height.toFloat()}")
        }

        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.W -> camera.position.y += shift
                    Input.Keys.A -> camera.position.x -= shift
                    Input.Keys.S -> camera.position.y -= shift
                    Input.Keys.D -> camera.position.x += shift
                    Input.Keys.SPACE -> isPause = !isPause
                    Input.Keys.F -> println(Gdx.graphics.framesPerSecond)
                }
                camera.update()
                return super.keyDown(keycode)
            }
        }
        Gdx.input.inputProcessor = handler
    }

    override fun render(delta: Float) {
        if (isPause) return
        Data.renderTime += delta
        frameBuffer.use {
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            batch.projectionMatrix = camera.combined
            batch.use {
                batch.draw(Scene2DSkin.defaultSkin.get<TextureRegion>(Drawables.DARK_HONEYCOMB()).texture, 0f, 0f, Gdx.graphics.width*scale, Gdx.graphics.height*scale)
                batch.draw(assets.manager.get<Texture>(Constants.GUN_DEFAULT), 0f, 0f, 300f*scale, 300f*scale)
                batch.draw(assets.manager.get<Texture>(Constants.SHIP_DEFAULT), (Gdx.graphics.width - 300f)*scale, (Gdx.graphics.height - 300f)*scale, 300f*scale, 300f*scale)
            }
        }

        Gdx.gl.glClearColor(assets.bgColor.r, assets.bgColor.g, assets.bgColor.b, assets.bgColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shader.use {
            shader.setUniformf("u_time", Data.renderTime)
            shader.setUniformf("u_mouse", Gdx.input.x.toFloat(), Gdx.graphics.height-Gdx.input.y.toFloat())
        }
        batch.shader = shader
        batch.use {
            val t = frameBuffer.colorBufferTexture
            // make repeat texture on edges instead of stretching edge pixels
            t.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
            // FBO flips texture for some reason, so you need to flip it back
            bufferTexture = TextureRegion(t).apply { flip(false, true) }
            batch.draw(bufferTexture, 0f, 0f, Gdx.graphics.width*scale, Gdx.graphics.height*scale)
        }
        batch.shader = null
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, width*scale, height*scale)
    }


    override fun dispose() {
        frameBuffer.dispose()
    }
}