package com.divelix.skitter.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.*
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.EntityBuilder
import ktx.app.KtxScreen
import java.util.*

class PlayScreen(game: Main): KtxScreen {
    companion object {
        var slowRate = Constants.DEFAULT_SLOW_RATE
        var isPaused = true
    }
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    private val dynamicData = DynamicData(Vector2(), Vector2(), 10, Array(10))

    private val world = World(Vector2(0f, 0f), true)
    private val engine = PooledEngine()
    private val entityBuilder = EntityBuilder(engine, world, assets)
    private val hud = Hud(game, dynamicData)
    private val blackList = ArrayList<Body>() // list of bodies to kill
//    private val b2dViewport: ScreenViewport
    private val camera: OrthographicCamera
    private val playerEntity: Entity

    init {
//        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
//        bgReg.setRegion(0, 0, Constants.WIDTH, Constants.HEIGHT)

        playerEntity = entityBuilder.createPlayer()
        camera = entityBuilder.createCamera(playerEntity)
//        b2dViewport = ScreenViewport(camera)
        entityBuilder.createEnemy(-3f, 7f, 1f, playerEntity)
        entityBuilder.createEnemy(0f, 7f, 1f, playerEntity)
        entityBuilder.createEnemy(3f, 7f, 1f, playerEntity)
//        createBorder()

        engine.addSystem(CameraSystem(dynamicData))
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(PhysicsSystem(world, blackList))
        engine.addSystem(PhysicsDebugSystem(world, camera))
        engine.addSystem(CollisionSystem(game))
        engine.addSystem(PlayerSystem(dynamicData))
        engine.addSystem(EnemySystem())
        engine.addSystem(BulletSystem())
        engine.addSystem(ClickableSystem(dynamicData, camera))

        ShaderProgram.pedantic = false

        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.SPACE -> isPaused = !isPaused
                    Input.Keys.B -> println(world.bodyCount)
                    Input.Keys.A -> println(dynamicData.aims)
                    Input.Keys.V -> println("HudCam: (${hud.camera.viewportWidth}; ${hud.camera.viewportHeight})")
                    Input.Keys.Z -> println("Table pos: (${hud.rootTable.x}; ${hud.rootTable.y})")
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(hud.stage, hud.playerCtrl, handler)
        Gdx.input.inputProcessor = multiplexer
        world.setContactListener(B2dContactListener())
    }

    override fun render(delta: Float) {
        engine.update(delta)
        if (!isPaused) {
            shootBullets(dynamicData.aims)
            clearDeadBodies()
        }
        hud.update(delta)
    }

    override fun show() {
        isPaused = true
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
//        isPaused = false
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, Constants.WIDTH, Constants.WIDTH * height/width)
        hud.camera.setToOrtho(false, width.toFloat(), height.toFloat())
        hud.widthRatio = width / Constants.WIDTH
        println("resize(): Resolution = ($width; $height) | HEIGHT = ${Constants.WIDTH * height/width} | widthRatio = ${hud.widthRatio}")
    }

    // hide?
    override fun dispose() {
        hud.dispose()
        engine.clearPools()
    }

    private fun shootBullets(aims: Array<Vector2>) {
        if (isPaused || aims.size == 0) return
        for (aim in aims) {
            if (dynamicData.ammo == 0) break
            entityBuilder.createBullet(playerEntity, aim)
            dynamicData.ammo--
            hud.ammoLabel.setText("${dynamicData.ammo}")
        }
        aims.clear()
    }

    private fun clearDeadBodies() {
        if (blackList.size > 0) {
            for (body in blackList) {
                val entity = body.userData as Entity?
                if (entity != null)
                    engine.removeEntity(entity)
                world.destroyBody(body)
            }
            blackList.clear()
        }
    }
}
