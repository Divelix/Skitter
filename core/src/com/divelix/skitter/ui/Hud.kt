package com.divelix.skitter.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divelix.skitter.*
import com.divelix.skitter.screens.GunScreen
import ktx.actors.*
import ktx.graphics.use
import ktx.vis.table

class Hud(val game: Main, val playCam: OrthographicCamera) {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val shape = context.inject<ShapeRenderer>()
    private val assets = context.inject<Assets>()

    val camera = OrthographicCamera()
    val stage = Stage(FillViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat(), camera), batch)

    private val aim = Vector2()
    private val aimTxt = assets.manager.get<Texture>(Constants.AIM)
    private val swordImage = Image(assets.manager.get<Texture>(Constants.WEAPON_ICON))
    val rootTable: Table
    lateinit var ammoLabel: Label

    var widthRatio = 1f // updates on first resize()
    var isDriven = false
    var isShipSlowdown = false
    val tempVec = Vector2()
    val fixedPoint = Vector3()
    val floatPoint = Vector3()
    val playerCtrl = object: InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            isShipSlowdown = false
            when (pointer) {
                0 -> {
                    fixedPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                    camera.unproject(fixedPoint)
                    floatPoint.set(fixedPoint) // fixes small bug
                }
                1 -> println("DON'T USE SECOND FINGER!!! ARE YOU CRAZY?")
            }
            return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            floatPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
            camera.unproject(floatPoint)
            tempVec.set(floatPoint.x, floatPoint.y).sub(fixedPoint.x, fixedPoint.y)
            if (tempVec.len2() > Constants.DEAD_BAND_2) {
                Data.dynamicData.dirVec.set(tempVec).scl(0.01f).limit(Constants.SPEED_LIMIT)
                swordImage.isVisible = false
//                PlayScreen.isPaused = false
                isDriven = true
            }
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            isShipSlowdown = true
            swordImage.isVisible = true
            if (isDriven) {
                isDriven = false
            } else {
                val click = playCam.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
                Data.dynamicData.aims.add(Vector2(click.x, click.y))
            }
//            PlayScreen.isPaused = true
            return true
        }
    }

    init {
        rootTable = table {
            setFillParent(true)
            top().right()
//            defaults().top()
            pad(20f)
            ammoLabel = label("${Data.dynamicData.ammo}") { cell->
                color = Color.ORANGE
                cell.fill()
            }
//            pack()
        }
        swordImage.setSize(64f, 128f)
        swordImage.setPosition(10f, 10f)
        swordImage.addListener(object: ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//                game.screen = GunScreen(game)
                println(Data.dynamicData.aims)
                return super.touchDown(event, x, y, pointer, button)
            }
        })

        stage += rootTable
        stage += swordImage
        stage.isDebugAll = true
    }

    fun update(delta: Float) {
        if(isDriven) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            shape.projectionMatrix = camera.combined
            shape.color = Color(0.2f, 1f, 0.2f, 0.5f)
            shape.begin(ShapeRenderer.ShapeType.Filled)
            shape.circle(fixedPoint.x, fixedPoint.y, 10f)
            shape.rectLine(fixedPoint.x, fixedPoint.y, floatPoint.x, floatPoint.y, 3f)
            shape.end()
            Gdx.gl.glDisable(GL20.GL_BLEND)
        }
        stage.act()
        stage.draw()

        if (isShipSlowdown) Data.dynamicData.dirVec.scl(0.95f)
    }

    fun dispose() {
        stage.dispose()
    }
}