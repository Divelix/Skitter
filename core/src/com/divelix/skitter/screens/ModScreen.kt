package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.divelix.skitter.*
import ktx.actors.plus
import ktx.app.KtxScreen
import ktx.vis.*

class ModScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val skin = assets.uiSkin
    private val stage = Stage(ScreenViewport(OrthographicCamera()), batch)

    init {
        batch.projectionMatrix = stage.camera.combined

//        val rootTable = table {
//            setFillParent(true)
//
//            tabbedPane { cell ->
//                cell.growY()
//                tab("Tab1") {
//                    label("Inside tab 1")
//                }
//                tab("Tab2") {
//                    label("Inside tab 2")
//                }
//                tab("Tab3") {
//                    label("Inside tab 3")
//                }
//            }.apply {
//                addTabContentsTo(table().cell(grow = true))
//                //OR addTabContentsTo(container<Table>().cell(grow = true))
//                switchTab(0)
//            }
//        }
//
//        stage + rootTable
        stage.isDebugAll = true
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun show() {
        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.SPACE -> {
                        println("SPACE")
                    }
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, stage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}