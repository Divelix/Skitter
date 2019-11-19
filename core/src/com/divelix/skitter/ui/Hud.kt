package com.divelix.skitter.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FillViewport
import com.divelix.skitter.*
import com.divelix.skitter.screens.PlayScreen
import com.kotcrab.vis.ui.VisUI
import ktx.actors.*
import ktx.graphics.*
import ktx.vis.table

class Hud(val game: Main, val playCam: OrthographicCamera) {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val shape = context.inject<ShapeRenderer>()
    private val assets = context.inject<Assets>()

    val camera = OrthographicCamera()
    val stage = Stage(FillViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat(), camera), batch)

    lateinit var fpsLabel: Label
    lateinit var renderTimeLabel: Label
    lateinit var physicsTimeLabel: Label
    lateinit var enemyCountLabel: Label
    lateinit var scoreLabel: Label
    val ammoLabel: Label

    val touchpadColor = Color(0.2f, 1f, 0.2f, 0.5f)
    val touchpadLimitColor = Color(1f, 0.2f, 0.2f, 0.5f)
    var activeColor = touchpadColor
    val healthColor = Color(1f, 0f, 0f, 1f)
    val reloadFGColor = Color(1f, 1f, 0f, 1f)
    val reloadBGColor = Color(1f, 1f, 0f, 0.3f)
    val reloadPos = Vector2(310f, 580f)

    var widthRatio = 1f // updates on first resize()
    var isDriven = false
    var isShipSlowdown = false
    val distVec = Vector2()
    val fixedPoint = Vector3()
    val floatPoint = Vector3()
    val playerCtrl = object: InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            isShipSlowdown = false
            when (pointer) {
                0 -> {
                    fixedPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                    camera.unproject(fixedPoint)
//                    floatPoint.set(fixedPoint) // fixes small bug // TODO che za bug?
                }
                1 -> {
                    val click = playCam.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                    Data.dynamicData.aims.add(Vector2(click.x, click.y))
                }
            }
            return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            when (pointer) {
                0 -> {
                    floatPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                    camera.unproject(floatPoint)
                    distVec.set(floatPoint.x, floatPoint.y).sub(fixedPoint.x, fixedPoint.y)
                    val dist2 = distVec.len2()
                    if (dist2 > Constants.DEAD_BAND_2) {
                        activeColor = if (dist2 < Constants.MAX_TOUCHPAD_RADIUS_2) touchpadColor else touchpadLimitColor
                        Data.dynamicData.dirVec.set(distVec).limit2(Constants.MAX_TOUCHPAD_RADIUS_2).scl(0.015f) // TODO assign scl() to ship speed spec
                        isDriven = true
                    }
                }
            }
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            when (pointer) {
                0 -> {
                    isShipSlowdown = true
                    if (isDriven) {
                        isDriven = false
                    } else {
                        val click = playCam.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                        Data.dynamicData.aims.add(Vector2(click.x, click.y))
                    }
                }
            }
            return true
        }
    }

    init {
        stage += table {
            setFillParent(true)
            top().pad(20f)
            defaults().expandX()
//            pad(20f)
            scoreLabel = label("${Data.score}", "mod-quantity").cell(colspan = 2)
            row()
            enemyCountLabel = label("${Data.enemiesCount}").cell(height = 50f, align = Align.left)
//            ammoLabel = label("${Data.playerData.gun.capacity}") { color = Color.ORANGE }.cell(align = Align.right)
            row()
            fpsLabel = label("${Gdx.graphics.framesPerSecond}").cell(align = Align.left)
            row()
            renderTimeLabel = label("${Data.renderTime}").cell(align = Align.left)
            row()
            physicsTimeLabel = label("${Data.physicsTime}").cell(align = Align.left)
        }
        ammoLabel = Label("${PlayScreen.ammo}", VisUI.getSkin(), "reload-label")
        stage += ammoLabel
//        stage.isDebugAll = true
    }

    val hpOffset = 20f
    val hpHeight = 10f
    fun update() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shape.projectionMatrix = camera.combined
        shape.use(ShapeRenderer.ShapeType.Filled) {
            if(isDriven) {
                shape.color = activeColor
                shape.circle(fixedPoint.x, fixedPoint.y, 10f)
                shape.rectLine(fixedPoint.x, fixedPoint.y, floatPoint.x, floatPoint.y, 3f)
            }
            shape.color = healthColor
            // TODO fix that healthbar hardcode after ship json implementation
            val barWidth = stage.width * Data.playerData.ship.health / 100f
            shape.rect(hpOffset + (stage.width - barWidth) / 2f, hpOffset,
                    barWidth - hpOffset*2, hpHeight)

            shape.color = reloadBGColor
            shape.circle(reloadPos, 30f)
            shape.color = reloadFGColor
            shape.arc(reloadPos, 30f, 90f, Data.reloadTimer / Data.playerData.gun.reloadTime * 360)
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)

        fpsLabel.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        renderTimeLabel.setText("Render time: ${Data.renderTime.toInt()}")
        physicsTimeLabel.setText("Physics time: ${Data.physicsTime.toInt()}")
        scoreLabel.setText("${Data.score}")
        enemyCountLabel.setText("Enemies: ${Data.enemiesCount}")
        ammoLabel.run {
            setText("${PlayScreen.ammo}")
            pack()
            setPosition(reloadPos.x - width/2f, reloadPos.y - height/2f)
        }
        stage.act()
        stage.draw()

        if (isShipSlowdown) Data.dynamicData.dirVec.scl(0.95f)
    }

    fun dispose() {
        stage.dispose()
    }
}