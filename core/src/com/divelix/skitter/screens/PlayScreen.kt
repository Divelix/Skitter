package com.divelix.skitter.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.divelix.skitter.*
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.DynamicData
import com.divelix.skitter.utils.EntityBuilder
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import java.util.*

class PlayScreen(game: Main): KtxScreen {
    companion object {
        var slowRate = Constants.DEFAULT_SLOW_RATE
        var isPaused = false
    }
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    private val world = World(Vector2(0f, 0f), true)
    private val engine = PooledEngine()
    private val entityBuilder = EntityBuilder(engine, world, assets)
    private val hud: Hud
    private val blackList = ArrayList<Body>() // list of bodies to kill
    private val camera: OrthographicCamera
    private val playerEntity: Entity

    init {
        Data.playerData.ship.health = 100f
        Data.playerData.ship.energy = 100f
        Data.playerData.ship.armor = 10f
        val playerReader = JsonReader().parse("json/player_data.json".toInternalFile())
        val specs = playerReader.get("active_gun_specs")
//        for (i in 0 until Data.playerData.gun.size)
//            Data.playerData.gun[i] = specs[i].asFloat()
        Data.playerData.gun.damage = specs[0].asFloat()
        Data.playerData.gun.capacity = specs[1].asInt()
        Data.playerData.gun.reloadTime = specs[2].asFloat()
        Data.playerData.gun.bulletSpeed = specs[3].asFloat()
        Data.playerData.gun.critChance = specs[4].asFloat()
        Data.playerData.gun.critMultiplier = specs[5].asFloat()

        playerEntity = entityBuilder.createPlayer()
        camera = entityBuilder.createCamera(playerEntity)
        hud = Hud(game, camera)
        entityBuilder.createEnemy(-3f, 7f, 2f, playerEntity)
        entityBuilder.createEnemy(0f, 7f, 2f, playerEntity)
        entityBuilder.createEnemy(3f, 7f, 2f, playerEntity)
        entityBuilder.createObstacle(5f, -3f, 2f, 2f)

        engine.addSystem(CameraSystem())
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(PhysicsSystem(world, blackList))
//        engine.addSystem(PhysicsDebugSystem(world, camera))
        engine.addSystem(CollisionSystem(game))
        engine.addSystem(PlayerSystem())
        engine.addSystem(EnemySystem())
        engine.addSystem(BulletSystem())
        engine.addSystem(SpawnSystem(5f, entityBuilder, playerEntity))
//        engine.addSystem(ClickableSystem(camera))

        ShaderProgram.pedantic = false

        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> game.screen = GunScreen(game)
                    Input.Keys.SPACE -> isPaused = !isPaused
                    Input.Keys.B -> println(world.bodyCount)
                    Input.Keys.A -> println(Data.dynamicData.aims)
                    Input.Keys.V -> println("HudCam: (${hud.camera.viewportWidth}; ${hud.camera.viewportHeight})")
                    Input.Keys.Z -> println("Table pos: (${hud.rootTable.x}; ${hud.rootTable.y})")
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, hud.stage, hud.playerCtrl)
        Gdx.input.inputProcessor = multiplexer
        world.setContactListener(B2dContactListener())
    }

    override fun render(delta: Float) {
        engine.update(delta)
        if (!isPaused) {
            shootBullets(Data.dynamicData.aims)
            clearDeadBodies()
        }
        hud.update()
    }

    override fun show() {
        println("PlayScreen - show()")
        isPaused = false
    }

    override fun pause() {
        println("PlayScreen - pause()")
        isPaused = true
    }

    override fun resume() {
        println("PlayScreen - resume()")
        isPaused = false
    }

    override fun resize(width: Int, height: Int) {
        println("PlayScreen - resize()")
        camera.setToOrtho(false, Constants.WIDTH, Constants.WIDTH * height/width)
//        hud.camera.setToOrtho(false, width.toFloat(), height.toFloat())
        hud.widthRatio = width / Constants.WIDTH
        hud.stage.viewport.update(width, height, true)
        println("resize(): Resolution = ($width; $height) | HEIGHT = ${Constants.WIDTH * height/width} | widthRatio = ${hud.widthRatio}")
    }

    override fun hide() {
        println("PlayScreen - hide()")
        super.hide()
    }

    override fun dispose() {
        println("PlayScreen - hide()")
        hud.dispose()
        engine.clearPools()
    }

    private fun shootBullets(aims: Array<Vector2>) {
        if (isPaused || aims.size == 0) return
        for (aim in aims) {
            if (Data.playerData.gun.capacity == 0) break
            entityBuilder.createBullet(playerEntity, aim)
            assets.manager.get<Sound>(Constants.SHOT_SOUND).play()
            Data.playerData.gun.capacity--
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
