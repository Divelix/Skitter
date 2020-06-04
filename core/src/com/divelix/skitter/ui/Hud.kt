package com.divelix.skitter.ui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FillViewport
import com.divelix.skitter.*
import com.divelix.skitter.screens.MenuScreen
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.DamageLabelProvider
import com.divelix.skitter.utils.EntityBuilder
import ktx.actors.*
import com.divelix.skitter.utils.LevelManager
import com.divelix.skitter.utils.ScaledLabel
import ktx.graphics.*
import ktx.log.info
import ktx.vis.table
import ktx.vis.window

class Hud(
        val game: Main,
        val entityBuilder: EntityBuilder,
        val playerEntity: Entity,
        val playCam: OrthographicCamera
) {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val shape = context.inject<ShapeRenderer>()
    private val assets = context.inject<Assets>()

    val hudCam = OrthographicCamera()
    val aspectRatio = Gdx.graphics.width.toFloat() / Gdx.graphics.height
    val hudStage = Stage(FillViewport(Constants.D_WIDTH.toFloat(), Constants.D_WIDTH / aspectRatio, hudCam), batch)
    val damageLabelsProvider = DamageLabelProvider(hudStage, playCam)

    private val rootTable: Table
    private val fpsLabel = ScaledLabel()
    private val renderTimeLabel = ScaledLabel()
    private val physicsTimeLabel = ScaledLabel()
    private val enemyCountLabel = ScaledLabel()
    private val scoreLabel = ScaledLabel(styleName = "score-label")
    private val ammoLabel =  ScaledLabel(styleName = "reload-label")

    private val touchpadColor = Color(0.2f, 1f, 0.2f, 0.5f)
    private val touchpadLimitColor = Color(1f, 0.2f, 0.2f, 0.5f)
    private var activeColor = touchpadColor
    private val healthBgColor = Color(1f, 0f, 0f, 0.1f)
    private val healthColor = Color(1f, 0f, 0f, 1f)
    private val reloadFGColor = Color(1f, 1f, 0f, 1f)
    private val reloadBGColor = Color(1f, 1f, 0f, 0.3f)
    private val scoreColor = Color(0.7f, 0.7f, 0.7f, 1f)
    private val reloadPos = Vector2(hudStage.width - 15f - 30f, hudStage.height - 20f - 50f - 20f - 30f)

    private val pauseBtn: Image
    private val pauseWindow: Window
    private val gameOverWindow: Window
    private val hpHeight = 10f
    private val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    private val healthBgImg = Image(Texture(pixel.apply { setColor(healthBgColor); fill() }))
    private val healthImg = Image(Texture(pixel.apply { setColor(healthColor); fill() }))

    val aimPos = Vector2()
    val clickPos = Vector3()
    var isDriven = false
    var isShipSlowdown = true
    val distVec = Vector2()
    val fixedPoint = Vector3()
    val floatPoint = Vector3()
    val playerCtrl = object: InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (GameEngine.isPaused) return false
            isShipSlowdown = false
            when (pointer) {
                0 -> {
                    fixedPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                    hudCam.unproject(fixedPoint)
//                    floatPoint.set(fixedPoint) // fixes small bug // TODO che za bug?
                }
                1 -> {
                    playCam.unproject(clickPos.set(screenX.toFloat(), screenY.toFloat(), 0f))
                    shoot(aimPos.set(clickPos.x, clickPos.y))
                }
            }
            return true
        }

        override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
            if (GameEngine.isPaused) return false
            when (pointer) {
                0 -> {
                    floatPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                    hudCam.unproject(floatPoint)
                    distVec.set(floatPoint.x, floatPoint.y).sub(fixedPoint.x, fixedPoint.y)
                    val dist = distVec.len()
                    if (dist > Constants.DEAD_BAND) {
                        activeColor = if (dist < Constants.MAX_TOUCHPAD_RADIUS) touchpadColor else touchpadLimitColor
                        Data.dirVec.set(distVec).limit(Constants.MAX_TOUCHPAD_RADIUS).scl(1f / Constants.MAX_TOUCHPAD_RADIUS)
                        isDriven = true
                    }
                }
            }
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (GameEngine.isPaused) return false
            when (pointer) {
                0 -> {
                    isShipSlowdown = true
                    if (isDriven) {
                        isDriven = false
                    } else {
                        playCam.unproject(clickPos.set(screenX.toFloat(), screenY.toFloat(), 0f))
                        shoot(aimPos.set(clickPos.x, clickPos.y))
                    }
                }
            }
            return true
        }
    }

    init {
        pauseWindow = makePauseWindow()
        gameOverWindow = makeGameOverWindow()
        pauseBtn = makePauseButton()
        rootTable = table {
            setFillParent(true)
            top().pad(10f)
            defaults().expandX()
//            pad(20f)
            add(scoreLabel).colspan(2)
            row()
            add(enemyCountLabel).height(50f).left()
            row()
            add(fpsLabel).left()
            row()
            add(renderTimeLabel).left()
            row()
            add(physicsTimeLabel).left()
//            row()
//            textButton("makeAgent") {
//                addListener(object : ClickListener() {
//                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//                        entityBuilder.createAgent(MathUtils.random(-10f, 10f), MathUtils.random(-10f, 40f))
//                        return super.touchDown(event, x, y, pointer, button)
//                    }
//                })
//            }.cell(align = Align.left)
        }

        hudStage += rootTable
        hudStage += ammoLabel
        hudStage += healthBgImg
        hudStage += healthImg
        hudStage += pauseBtn
        hudStage += pauseWindow
        hudStage += gameOverWindow
//        hudStage.isDebugAll = true

        healthBgImg.run {
            setSize(stage.width * 0.9f, hpHeight)
            setPosition(stage.width * 0.05f, stage.width * 0.05f)
        }
        healthImg.run {
            setSize(healthBgImg.width, healthBgImg.height)
            setPosition(healthBgImg.x, healthBgImg.y)
        }
    }

    fun shoot(aim: Vector2) {
        if (PlayScreen.ammo <= 0) return
        entityBuilder.createPlayerBullet(playerEntity, aim)
        assets.manager.get<Sound>(Constants.SHOT_SOUND).play()
        if (PlayScreen.ammo == Data.playerData.gun.capacity) Data.reloadTimer = 0f // fix for reload on first shot
        PlayScreen.ammo--
    }

    fun update() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shape.projectionMatrix = hudCam.combined
        shape.use(ShapeRenderer.ShapeType.Filled) {
            if(isDriven) {
                shape.color = activeColor
                shape.circle(fixedPoint.x, fixedPoint.y, 10f)
                shape.rectLine(fixedPoint.x, fixedPoint.y, floatPoint.x, floatPoint.y, 3f)
            }
            shape.color = scoreColor
            shape.circle(175f, 725f, 60f)
            shape.color = reloadBGColor
            shape.circle(reloadPos, 30f)
            shape.color = reloadFGColor
            shape.arc(reloadPos, 30f, 90f, Data.reloadTimer / Data.playerData.gun.reloadTime * 360)
        }
        healthImg.run {
            val hpWidth = stage.width * PlayScreen.health / Data.playerData.ship.health
            width = hpWidth * 0.9f
            x = hpWidth * 0.05f + (stage.width - hpWidth) / 2f
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)

        fpsLabel.setText("FPS: ${Gdx.graphics.framesPerSecond}")
        renderTimeLabel.setText("Render time: ${Data.renderTime.toInt()}")
        physicsTimeLabel.setText("Physics time: ${Data.physicsTime.toInt()}")
        scoreLabel.setText("${Data.score}")
        enemyCountLabel.setText("Enemies: ${LevelManager.enemiesCount}")
        ammoLabel.run {
            setText("${PlayScreen.ammo}")
            pack()
            setPosition(reloadPos.x - width/2f, reloadPos.y - height/2f)
        }

        hudStage.act()
        hudStage.draw()

        if (isShipSlowdown && Data.dirVec.len2() > 0.0001f) Data.dirVec.scl(0.1f)
    }

    fun resize(width: Int, height: Int) {
        Gdx.app.log("Hud","resize: $width; $height")
        hudStage.viewport.update(width, height, true)
    }

    fun dispose() {
        hudStage.dispose()
    }

    private fun makePauseButton(): Image {
        return Image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.PAUSE_BTN))).apply {
            setSize(50f, 50f)
            setPosition(hudStage.width - width - 20f, hudStage.height - height - 20f)
            addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    GameEngine.isPaused = true
                    pauseWindow.isVisible = true
//                    Data.renderTime = 0f
//                    Data.physicsTime = 0f
//                    game.screen = MenuScreen(game)
                    super.touchUp(event, x, y, pointer, button)
                }
            })
        }
    }

    private fun makePauseWindow(): Window {
        return window("Pause") {
            isVisible = false
            debugAll()
            centerWindow()
            defaults().expand()
            padTop(25f) // title height
            width = 200f
            height = 100f
//            val quantityLabel = label("retwert").cell(colspan = 2)
            row()
            textButton("Exit").cell(align = Align.left).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    LevelManager.isNextLvlRequired = true
                    game.screen = MenuScreen(game)
                }
            })
            textButton("Resume").cell(align = Align.right).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    GameEngine.isPaused = false
                    isVisible = false
                }
            })
        }
    }

    private fun makeGameOverWindow(): Window {
        return window("Game Over") {
            isVisible = false
            debugAll()
            centerWindow()
            defaults().expand()
            padTop(25f) // title height
            width = 300f
            height = 500f
            row()
            label("table of results").cell(colspan = 2)
            row()
            textButton("Restart").cell(align = Align.right).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
                    isVisible = false
                }
            })

            textButton("Exit").cell(align = Align.left).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    LevelManager.isNextLvlRequired = true
                    GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
                    game.screen = MenuScreen(game)
                }
            })
        }
    }

    fun showGameOverWindow() {
        gameOverWindow.isVisible = true
    }

    companion object {
        private val TAG = Hud::class.simpleName
    }
}