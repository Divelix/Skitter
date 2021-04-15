package com.divelix.skitter.ui.hud

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.*
import com.divelix.skitter.data.*
import com.divelix.skitter.gameplay.EntityBuilder
import com.divelix.skitter.gameplay.GameEngine
import com.divelix.skitter.gameplay.LevelManager
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.screens.ScrollMenuScreen
import com.divelix.skitter.utils.TopViewport
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.*
import ktx.graphics.*
import ktx.collections.*
import ktx.scene2d.*
import ktx.scene2d.vis.*
import ktx.style.get

class Hud(
        val game: Main,
        private val activePlayerData: ActivePlayerData,
        val entityBuilder: EntityBuilder,
        val playerEntity: Entity,
        val playCam: OrthographicCamera
) {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val shape = context.inject<ShapeRenderer>()
    private val assets = context.inject<Assets>()

    val hudCam = OrthographicCamera()
    val hudStage = Stage(TopViewport(Constants.STAGE_WIDTH.toFloat(), Constants.stageHeight, hudCam), batch)
    val damageLabelsProvider = DamageLabelProvider(hudStage, playCam)

    private val rootTable: Table
    private val fpsLabel: Label
    private val renderTimeLabel: Label
    private val physicsTimeLabel: Label
    private val enemyCountLabel: Label
    private val scoreLabel: Label
    private val ammoLabel: Label
//    private val scoreLabel = ScaledLabel(styleName = "score-label")
//    private val ammoLabel = ScaledLabel(styleName = "reload-label")

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
    val playerCtrl = object : InputAdapter() {
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
//        hudStage.isDebugAll = true
        pauseWindow = makePauseWindow()
        pauseBtn = makePauseButton()
        rootTable = scene2d.table {
            setFillParent(true)
            top().pad(10f)
            defaults().expandX().left()
            scoreLabel = scaledLabel("", 0.2f, style = Constants.STYLE_BLACK_LABEL).cell(colspan = 2, align = Align.center)
            row()
            enemyCountLabel = scaledLabel("").cell(height = 50f)
            row()
            fpsLabel = scaledLabel("")
            row()
            renderTimeLabel = scaledLabel("")
            row()
            physicsTimeLabel = scaledLabel("")
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
        ammoLabel = scene2d.scaledLabel("", 0.3f, style = Constants.STYLE_BLACK_LABEL)

        hudStage += rootTable
        hudStage += ammoLabel
        hudStage += healthBgImg
        hudStage += healthImg
        hudStage += pauseBtn
        hudStage += pauseWindow

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
        if (PlayScreen.ammo == activePlayerData.gunCapacity) Data.reloadTimer = 0f // fix for reload on first shot
        PlayScreen.ammo--
    }

    fun update() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shape.projectionMatrix = hudCam.combined
        shape.use(ShapeRenderer.ShapeType.Filled) {
            if (isDriven) {
                shape.color = activeColor
                shape.circle(fixedPoint.x, fixedPoint.y, 10f)
                shape.rectLine(fixedPoint.x, fixedPoint.y, floatPoint.x, floatPoint.y, 3f)
            }
            shape.color = scoreColor
            shape.circle(175f, 725f, 60f)
            shape.color = reloadBGColor
            shape.circle(reloadPos, 30f)
            shape.color = reloadFGColor
            shape.arc(reloadPos, 30f, 90f, Data.reloadTimer / activePlayerData.gunReload * 360)
        }
        healthImg.run {
            val hpWidth = stage.width * PlayScreen.health / activePlayerData.shipHealth
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
            setPosition(reloadPos.x - width / 2f, reloadPos.y - height / 2f)
        }

        hudStage.act()
        hudStage.draw()

        if (isShipSlowdown && Data.dirVec.len2() > 0.0001f) Data.dirVec.scl(0.1f)
    }

    fun resize(width: Int, height: Int) {
        hudStage.viewport.update(width, height)
    }

    fun dispose() {
        hudStage.dispose()
    }

    private fun makePauseButton(): Image {
        return Image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.PAUSE()))).apply {
            setSize(50f, 50f)
            setPosition(hudStage.width - width - 20f, hudStage.height - height - 20f)
            addListener(object : ClickListener() {
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
        return scene2d.visWindow("Pause") {
            titleLabel.setFontScale(0.2f)
            isVisible = false
            debugAll()
            centerWindow()
            defaults().expand()
            padTop(30f) // title height
            width = 300f
            height = 500f
            imageButton(Constants.STYLE_EXIT_BTN)
                    .cell(align = Align.center, height = 50f)
                    .onTouchDown {
                        LevelManager.isNextLvlRequired = true
                        game.screen = ScrollMenuScreen(game)
                    }
            imageButton(Constants.STYLE_RESTART_BTN)
                    .cell(align = Align.center, height = 50f)
                    .onTouchDown {
                        GameEngine.isPaused = false
                        isVisible = false
                    }
        }
    }

    private fun makeStatsTable(): Table {
        return scene2d.table {
            val iconWidth = 50f
            val cellWidth = 150f
            debug = true
            padTop(25f)
            defaults().padTop(10f)
            Data.matchHistory.forEach { (enemyType, quantity) ->
                require(quantity != null)
                val region = Scene2DSkin.defaultSkin.get<TextureRegion>(findEnemyTexturePath(enemyType))
                val ratio = region.regionWidth.toFloat() / region.regionHeight
                image(region).cell(width = iconWidth, height = iconWidth / ratio)
                label("x${quantity}")
                label("${quantity * 10}").cell(width = cellWidth).setAlignment(Align.center)
                row()
            }
        }
    }

    private fun endWindow(title: String, content: KVisWindow.() -> Unit): VisWindow {
        return scene2d.visWindow(title) {
            debugAll()
            centerWindow()
            padTop(50f) // title height
            defaults().top()
            width = 320f
            height = 500f
            row()
            content()
        }
    }

    private fun makeVictoryWindow(): VisWindow {
        return endWindow("Victory") {
            // Stats table
            add(makeStatsTable()).expand(true, true)
            row()
//            add(ImgBgButton(assets, assets.manager.get<Texture>(Constants.HOME_ICON)) {
//                LevelManager.isNextLvlRequired = true
//                GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
//                game.screen = ScrollMenuScreen(game)
//            })
        }
    }

    private fun makeGameOverWindow(): VisWindow {
        return endWindow("Game Over") {
            // Stats table
            add(makeStatsTable()).colspan(2).expand(true, true)
            row()
//            add(ImgBgButton(assets, assets.manager.get<Texture>(Constants.RESTART_ICON)) {
//                GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
//                // TODO make restart
//            })
//            add(ImgBgButton(assets, assets.manager.get<Texture>(Constants.HOME_ICON)) {
//                LevelManager.isNextLvlRequired = true
//                GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
//                game.screen = ScrollMenuScreen(game)
//            })
        }
    }

    private fun findEnemyTexturePath(enemyType: Enemy): String {
        return when (enemyType) {
            Enemy.AGENT -> RegionName.AGENT()
            Enemy.JUMPER -> RegionName.JUMPER()
            Enemy.SNIPER -> RegionName.SNIPER_BASE()
            Enemy.WOMB -> RegionName.WOMB()
            Enemy.KID -> RegionName.KID()
            Enemy.RADIAL -> RegionName.RADIAL()
        }
    }

    fun showGameOverWindow() {
        hudStage += makeGameOverWindow()
        // TODO save match history
    }

    fun showVictoryWindow() {
        hudStage += makeVictoryWindow()
        // TODO save match history
    }

    companion object {
        private val TAG = Hud::class.simpleName
    }
}