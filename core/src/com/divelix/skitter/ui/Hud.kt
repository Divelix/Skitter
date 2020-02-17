package com.divelix.skitter.ui

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.viewport.FillViewport
import com.divelix.skitter.*
import com.divelix.skitter.screens.MenuScreen
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.EntityBuilder
import com.kotcrab.vis.ui.VisUI
import ktx.actors.*
import com.divelix.skitter.components.DamageLabelComponent
import com.divelix.skitter.components.TransformComponent
import ktx.ashley.mapperFor
import ktx.graphics.*
import ktx.vis.table
import ktx.vis.window

class Hud(val game: Main, val playCam: OrthographicCamera, val entityBuilder: EntityBuilder, val playerEntity: Entity) {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val shape = context.inject<ShapeRenderer>()
    private val assets = context.inject<Assets>()

    val camera = OrthographicCamera()
    val aspectRatio = Gdx.graphics.height.toFloat() /Gdx.graphics.width
    val hudStage = Stage(FillViewport(Constants.D_WIDTH.toFloat(), Constants.D_WIDTH * aspectRatio, camera), batch)

    private val rootTable: Table
    lateinit var fpsLabel: Label
    lateinit var renderTimeLabel: Label
    lateinit var physicsTimeLabel: Label
    lateinit var enemyCountLabel: Label
    lateinit var scoreLabel: Label
    private val ammoLabel: Label
    val damageLabelsPool = DamageLabelsPool()

    private val touchpadColor = Color(0.2f, 1f, 0.2f, 0.5f)
    private val touchpadLimitColor = Color(1f, 0.2f, 0.2f, 0.5f)
    private var activeColor = touchpadColor
    private val healthBgColor = Color(1f, 0f, 0f, 0.1f)
    private val healthColor = Color(1f, 0f, 0f, 1f)
    private val reloadFGColor = Color(1f, 1f, 0f, 1f)
    private val reloadBGColor = Color(1f, 1f, 0f, 0.3f)
    private val scoreColor = Color(0.7f, 0.7f, 0.7f, 1f)
    private val reloadPos = Vector2(310f, 580f)

    private val pauseBtn: Image
    private val pauseWindow: Window
    private val hpHeight = 10f
    private val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    private val healthBgImg = Image(Texture(pixel.apply { setColor(healthBgColor); fill() }))
    private val healthImg = Image(Texture(pixel.apply { setColor(healthColor); fill() }))

    val temp = Vector3()
    private val cmDamage = mapperFor<DamageLabelComponent>()
    private val cmTrans = mapperFor<TransformComponent>()
    val aimPos = Vector2()
    val clickPos = Vector3()
    var isDriven = false
    var isShipSlowdown = true
    val distVec = Vector2()
    val fixedPoint = Vector3()
    val floatPoint = Vector3()
    val playerCtrl = object: InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (PlayScreen.isPaused) return false
            isShipSlowdown = false
            when (pointer) {
                0 -> {
                    fixedPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                    camera.unproject(fixedPoint)
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
            if (PlayScreen.isPaused) return false
            when (pointer) {
                0 -> {
                    floatPoint.set(screenX.toFloat(), screenY.toFloat(), 0f)
                    camera.unproject(floatPoint)
                    distVec.set(floatPoint.x, floatPoint.y).sub(fixedPoint.x, fixedPoint.y)
                    val dist2 = distVec.len2()
                    if (dist2 > Constants.DEAD_BAND_2) {
                        activeColor = if (dist2 < Constants.MAX_TOUCHPAD_RADIUS_2) touchpadColor else touchpadLimitColor
                        Data.dirVec.set(distVec).limit2(Constants.MAX_TOUCHPAD_RADIUS_2).scl(0.015f) // TODO assign scl() to ship speed spec
                        isDriven = true
                    }
                }
            }
            return true
        }

        override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            if (PlayScreen.isPaused) return false
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
        pauseBtn = makePauseButton()
        rootTable = table {
            setFillParent(true)
            top().pad(10f)
            defaults().expandX()
//            pad(20f)
            scoreLabel = label("${Data.score}", "score-label").cell(colspan = 2)
            row()
            enemyCountLabel = label("${Data.enemiesCount}").cell(height = 50f, align = Align.left)
//            ammoLabel = label("${Data.playerData.gun.capacity}") { color = Color.ORANGE }.cell(align = Align.right)
            row()
            fpsLabel = label("${Gdx.graphics.framesPerSecond}").cell(align = Align.left)
            row()
            renderTimeLabel = label("${Data.renderTime}").cell(align = Align.left)
            row()
            physicsTimeLabel = label("${Data.physicsTime}").cell(align = Align.left)
            row()
            textButton("makeAgent") {
                addListener(object : ClickListener() {
                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        entityBuilder.createAgent(MathUtils.random(-10f, 10f), MathUtils.random(-10f, 40f))
                        return super.touchDown(event, x, y, pointer, button)
                    }
                })
            }.cell(align = Align.left)
        }
        ammoLabel = Label("${PlayScreen.ammo}", VisUI.getSkin(), "reload-label").apply {
            setFontScale(0.5f)
        }

        hudStage += rootTable
        hudStage += ammoLabel
        hudStage += healthBgImg
        hudStage += healthImg
        hudStage += pauseBtn
        hudStage += pauseWindow
//        stage.isDebugAll = true

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
        entityBuilder.createBullet(playerEntity, aim)
        assets.manager.get<Sound>(Constants.SHOT_SOUND).play()
        if (PlayScreen.ammo == Data.playerData.gun.capacity) Data.reloadTimer = 0f // fix for reload on first shot
        PlayScreen.ammo--
    }

    fun update() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shape.projectionMatrix = camera.combined
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
        enemyCountLabel.setText("Enemies: ${Data.enemiesCount}")
        ammoLabel.run {
            setText("${PlayScreen.ammo}")
            pack()
            setPosition(reloadPos.x - width/2f, reloadPos.y - height/2f)
        }

        hudStage.act()
        hudStage.draw()

        if (isShipSlowdown) Data.dirVec.scl(0.95f)
    }

    fun resize(width: Int, height: Int) {
        Gdx.app.log("Hud","resize: $width; $height")
        hudStage.viewport.update(width, height, true)
//        camera.setToOrtho(false, width.toFloat(), height.toFloat())
    }

    fun dispose() {
        hudStage.dispose()
    }

    private fun makePauseButton(): Image {
        return Image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.PAUSE_BTN))).apply {
            setSize(50f, 50f)
            setPosition(Constants.D_WIDTH - width - 20f, Constants.D_HEIGHT - height - 20f)
            addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    PlayScreen.isPaused = true
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
                    game.screen = MenuScreen(game)
                }
            })
            textButton("Resume").cell(align = Align.right).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    PlayScreen.isPaused = false
                    isVisible = false
                }
            })
        }
    }

    fun makeDamageLabel(damage: Float, damagedEntity: Entity) {
        damageLabelsPool.obtain().run {
            txt = "${damage.toInt()}"
            temp.set(cmTrans.get(damagedEntity).position)
            playCam.project(temp)
            val ratio = Constants.D_WIDTH / Gdx.graphics.width.toFloat()
            temp.scl(ratio)
            prevPos.set(temp.x, temp.y)
            setPosition(temp.x, temp.y)
            hudStage += this
            cmDamage.get(damagedEntity).damageLabels.add(this)
            animate()
        }
    }

    inner class DamageLabelsPool(initialCapacity: Int = 10, max: Int = 20): Pool<DamageLabel>(initialCapacity, max) {
        override fun newObject(): DamageLabel {
            return DamageLabel()
        }
    }

    inner class DamageLabel: Label("", VisUI.getSkin()), Pool.Poolable {
        val duration = 1f
        var ecsTimer = duration
        val prevPos = Vector2()
        val latestPos = Vector3()
        private val shift = Vector2()

        override fun reset() {
            ecsTimer = duration
        }

        override fun act(delta: Float) {
            moveTo(latestPos)
            super.act(delta)
        }

        fun animate() {
            val removeAction = Actions.run {
                remove()
                damageLabelsPool.free(this)
            }
            val alphaAnim = Actions.alpha(0f) then Actions.fadeIn(duration / 2f) then Actions.fadeOut(duration / 2f)
            val moveAnim = Actions.moveBy(0f, 40f, duration)
            val removeAnim = Actions.delay(duration) then removeAction
            this += alphaAnim along moveAnim along removeAnim
        }

        private fun moveTo(point: Vector3) {
            temp.set(point)
            playCam.project(temp)
            val ratio = Constants.D_WIDTH / Gdx.graphics.width.toFloat()
            temp.scl(ratio)

            shift.set(temp.x, temp.y).sub(prevPos)
            moveBy(shift.x, shift.y)
            prevPos.set(temp.x, temp.y)
        }
    }
}