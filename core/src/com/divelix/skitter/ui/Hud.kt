package com.divelix.skitter.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FillViewport
import com.divelix.skitter.*
import com.divelix.skitter.screens.MenuScreen
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

    private val swordImage = Image(assets.manager.get<Texture>(Constants.WEAPON_ICON))
    val rootTable: Table
    lateinit var fpsLabel: Label
    lateinit var renderTimeLabel: Label
    lateinit var physicsTimeLabel: Label
    lateinit var enemyCountLabel: Label
    lateinit var scoreLabel: Label
    lateinit var ammoLabel: Label

    val touchpadColor = Color(0.2f, 1f, 0.2f, 0.5f)
    val touchpadLimitColor = Color(1f, 0.2f, 0.2f, 0.5f)
    val healthColor = Color(1f, 0f, 0f, 1f)
    var activeColor = touchpadColor

    var camBounceTimer = 0f

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
                        activeColor = if (dist2 < 10000) touchpadColor else touchpadLimitColor // TODO tie up limit (10000) with SPEED_LIMIT constant
                        Data.dynamicData.dirVec.set(distVec).scl(0.01f).limit(Constants.SPEED_LIMIT)
                        swordImage.isVisible = false
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
                    swordImage.isVisible = true
                    if (isDriven) {
                        isDriven = false
                    } else {
                        val click = playCam.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                        Data.dynamicData.aims.add(Vector2(click.x, click.y))
                        camBounceTimer = 1f
                    }
                }
            }
            return true
        }
    }

    init {
        rootTable = table {
            setFillParent(true)
            top().left()
            defaults().fill()
            pad(20f)
            fpsLabel = label("${Gdx.graphics.framesPerSecond}")
            row()
            renderTimeLabel = label("${Data.renderTime}")
            row()
            physicsTimeLabel = label("${Data.physicsTime}")
            row()
            scoreLabel = label("${Data.score}", "mod-quantity")
            row()
            enemyCountLabel = label("${Data.enemiesCount}")
            row()
            ammoLabel = label("${Data.playerData.gun.capacity}") { color = Color.ORANGE }
        }
        swordImage.setSize(64f, 128f)
        swordImage.setPosition(10f, 10f)
        swordImage.addListener(object: ClickListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                Data.dynamicData.dirVec.setZero()
                game.screen = MenuScreen(game)
                super.touchUp(event, x, y, pointer, button)
            }
        })

        stage += rootTable
        stage += swordImage
        stage.isDebugAll = true
    }

    val hpOffset = 10f
    val hpHeight = 10f
    fun update() {
        fpsLabel.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        renderTimeLabel.setText("Render time: ${Data.renderTime.toInt()}")
        physicsTimeLabel.setText("Physics time: ${Data.physicsTime.toInt()}")
        scoreLabel.setText("Score: ${Data.score}")
        enemyCountLabel.setText("Enemies: ${Data.enemiesCount}")
        ammoLabel.setText("${Data.playerData.gun.capacity}")

        Gdx.gl.glEnable(GL20.GL_BLEND)
        shape.projectionMatrix = camera.combined
        shape.use(ShapeRenderer.ShapeType.Filled) {
            if(isDriven) {
                shape.color = activeColor
                shape.circle(fixedPoint.x, fixedPoint.y, 10f)
                shape.rectLine(fixedPoint.x, fixedPoint.y, floatPoint.x, floatPoint.y, 3f)
            }
            shape.color = healthColor
            // TODO fix that hardcode after ship json implementation
            val barWidth = Gdx.graphics.width * Data.playerData.ship.health / 100f
            shape.rect(hpOffset + (Gdx.graphics.width - barWidth) / 2f, hpOffset,
                    barWidth - hpOffset*2, hpHeight)
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)
//        bounceCam()
        stage.act()
        stage.draw()

        if (isShipSlowdown) Data.dynamicData.dirVec.scl(0.95f)
    }

    fun bounceCam() {
        if (camBounceTimer > 0f) camBounceTimer -= Gdx.graphics.deltaTime
        val reversed = 1 - camBounceTimer
        val value = if (reversed < 0.5f) Interpolation.exp5Out.apply(reversed * 2f) else Interpolation.linear.apply((1 - reversed) * 2f)
        playCam.zoom = 1 + value / 10f
    }

    fun dispose() {
        stage.dispose()
    }
}