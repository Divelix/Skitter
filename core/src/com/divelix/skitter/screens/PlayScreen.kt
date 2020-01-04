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
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.EntityBuilder
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.log.info
import java.util.*

class PlayScreen(val game: Main): KtxScreen {
    companion object {
        var slowRate = Constants.DEFAULT_SLOW_RATE
        var isPaused = false
        var ammo = 0
        var health = 0
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
        isPaused = false
        Data.score = 0
        Data.enemiesCount = 0
        val playerReader = JsonReader().parse(Constants.PLAYER_FILE.toInternalFile())
        val shipSpecs = playerReader.get("active_ship_specs")
        Data.playerData.ship.health = shipSpecs[0].asFloat()
        Data.playerData.ship.speed = shipSpecs[1].asFloat()
        val gunSpecs = playerReader.get("active_gun_specs")
        Data.playerData.gun.damage = gunSpecs[0].asFloat()
        Data.playerData.gun.capacity = gunSpecs[1].asInt()
        Data.playerData.gun.reloadTime = gunSpecs[2].asFloat()
        Data.playerData.gun.bulletSpeed = gunSpecs[3].asFloat()
        Data.playerData.gun.critChance = gunSpecs[4].asFloat()
        Data.playerData.gun.critMultiplier = gunSpecs[5].asFloat()
        ammo = Data.playerData.gun.capacity
        Data.dynamicData.dirVec.set(0f, 0.000001f)// little init movement fixes 90deg ship rotation on init

        entityBuilder.createBattleground(-8f, -8f, 16f, 45f)
        entityBuilder.createObstacle(-6f, 5f, 2f, 4f)
        entityBuilder.createObstacle(6f, 5f, 2f, 4f)
        entityBuilder.createPuddle(0f, 5f, 2f)
        entityBuilder.createSpawn(0f, 10f, 2f)
        entityBuilder.createObstacle(8f, -4f, 2f, 2f)
        playerEntity = entityBuilder.createPlayer(Data.playerData.ship.health)
        camera = entityBuilder.createCamera(playerEntity)
        hud = Hud(game, camera)

        engine.addSystem(CameraSystem())
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(PhysicsSystem(world, blackList))
        engine.addSystem(MovementSystem())
        engine.addSystem(PhysicsDebugSystem(world, camera))
        engine.addSystem(CollisionSystem(game))
        engine.addSystem(PlayerSystem())
        engine.addSystem(EnemySystem())
        engine.addSystem(BulletSystem())
        engine.addSystem(SpawnSystem(5f, entityBuilder, playerEntity))
        engine.addSystem(DecaySystem(0.1f))
        engine.addSystem(RegenerationSystem(0.5f))
        engine.addSystem(SlowSystem())
//        engine.addSystem(ClickableSystem(camera))

        ShaderProgram.pedantic = false

        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> game.screen = MenuScreen(game)
                    Input.Keys.SPACE -> isPaused = !isPaused
                    Input.Keys.B -> println(world.bodyCount)
                    Input.Keys.A -> println(Data.dynamicData.aims)
                    Input.Keys.V -> println("HudCam: (${hud.camera.viewportWidth}; ${hud.camera.viewportHeight})")
                    Input.Keys.D -> playerEntity.add(DecayComponent())
                    Input.Keys.S -> playerEntity.remove(DecayComponent::class.java)
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
        if (health <= 0f) gameOver()
    }

    override fun pause() {
        info("PlayScreen") { "pause()" }
        isPaused = true
    }

    override fun resume() {
        info("PlayScreen") { "resume()" }
        isPaused = false
    }

    override fun resize(width: Int, height: Int) {
        info("PlayScreen") { "resize()" }
        camera.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT)
        hud.resize(width, height)
    }

    override fun hide() {
        info("PlayScreen") { "hide()" }
    }

    override fun dispose() {
        info("PlayScreen") { "dispose()" }
        hud.dispose()
        engine.clearPools()
    }

    private fun shootBullets(aims: Array<Vector2>) {
        if (isPaused || aims.size == 0) return
        for (aim in aims) {
            if (ammo == 0) break
            entityBuilder.createBullet(playerEntity, aim)
            assets.manager.get<Sound>(Constants.SHOT_SOUND).play()
            if (ammo == Data.playerData.gun.capacity) Data.reloadTimer = 0f // fix for reload on first shot
            ammo--
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

    private fun gameOver() {
        println("------------------------------------")
        println("-------------Game Over--------------")
        println("------------------------------------")
        game.screen = MenuScreen(game)
    }
}
