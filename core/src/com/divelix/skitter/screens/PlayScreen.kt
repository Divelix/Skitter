package com.divelix.skitter.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.JsonReader
import com.divelix.skitter.*
import com.divelix.skitter.components.CameraComponent
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.EntityBuilder
import com.divelix.skitter.utils.LevelManager
import ktx.app.KtxScreen
import ktx.assets.toLocalFile
import ktx.box2d.body
import ktx.log.info
import java.util.*

class PlayScreen(val game: Main): KtxScreen {
    companion object {
        var slowRate = Constants.DEFAULT_SLOW_RATE
        var isPaused = false
        var ammo = 0
        var health = 0f
    }
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    private val world = World(Vector2(0f, 0f), true)
    private val debugRenderer = Box2DDebugRenderer()
    private val engine = PooledEngine()
    private val entityBuilder = EntityBuilder(engine, world, assets)
    private val camera: OrthographicCamera // follows player
    private val playerEntity: Entity
    private val hud: Hud
    private val blackList = ArrayList<Body>() // list of bodies to kill
    private val levelManager: LevelManager

    init {
        Data.renderTime = 0f
        Data.physicsTime = 0f
        Data.score = 0
        LevelManager.enemiesCount = 0
        Data.dirVec.set(0f, 0.000001f)// little init movement fixes 90deg ship rotation on init
        isPaused = false

        loadPlayerData()

        playerEntity = entityBuilder.createPlayer(5f, 2f)
        val cameraEntity = entityBuilder.createCamera(playerEntity)
        camera = cameraEntity.getComponent(CameraComponent::class.java).camera
        hud = Hud(game, camera, entityBuilder, playerEntity)
        levelManager = LevelManager(game, entityBuilder, playerEntity, cameraEntity)

        createEngineSystems()

        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK, Input.Keys.ESCAPE  -> game.screen = MenuScreen(game)
                    Input.Keys.SPACE -> isPaused = !isPaused
                    Input.Keys.B -> println(world.bodyCount)
                    Input.Keys.C -> println(camera.position)
//                    Input.Keys.V -> println("HudCam: (${hud.camera.viewportWidth}; ${hud.camera.viewportHeight})")
                    Input.Keys.D -> playerEntity.add(DecayComponent())
                    Input.Keys.S -> playerEntity.remove(DecayComponent::class.java)
                    Input.Keys.Z -> entityBuilder.createAgent(0f, 10f)
                    Input.Keys.A -> entityBuilder.createAgent(MathUtils.random(-10f, 10f), MathUtils.random(-10f, 40f))
                    Input.Keys.J -> entityBuilder.createJumper(MathUtils.random(-10f, 10f), MathUtils.random(-10f, 40f))
                    Input.Keys.N -> levelManager.goToNextLevel()
                    Input.Keys.L -> println(levelManager.levelEntities.size)
                    Input.Keys.M -> entityBuilder.createWall(Vector2(1f, 1f), Vector2(2f, 3f))
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, hud.hudStage, hud.playerCtrl)
        Gdx.input.inputProcessor = multiplexer
        world.setContactListener(B2dContactListener(game, hud))
    }

    override fun render(delta: Float) {
        if (!isPaused) {
            clearDeadBodies()
            if (health <= 0f) gameOver()
        }
        levelManager.update()
        engine.update(delta)
//        debugRenderer.render(world, camera.combined)
        hud.update()
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

    private fun loadPlayerData() {
        val playerReader = JsonReader().parse(Constants.PLAYER_FILE.toLocalFile())
        val shipSpecs = playerReader.get("active_ship_specs")
        Data.playerData.ship.health = shipSpecs[0].asFloat()
        health = Data.playerData.ship.health
        Data.playerData.ship.speed = shipSpecs[1].asFloat()
        val gunSpecs = playerReader.get("active_gun_specs")
        Data.playerData.gun.damage = gunSpecs[0].asFloat()
        Data.playerData.gun.capacity = gunSpecs[1].asInt()
        Data.playerData.gun.reloadTime = gunSpecs[2].asFloat()
        Data.playerData.gun.bulletSpeed = gunSpecs[3].asFloat()
        Data.playerData.gun.critMultiplier = gunSpecs[4].asFloat()
        Data.playerData.gun.critChance = gunSpecs[5].asFloat()
        ammo = Data.playerData.gun.capacity
    }

    private fun createEngineSystems() {
        engine.addSystem(CameraSystem())
        engine.addSystem(PhysicsSystem(world, blackList))
        engine.addSystem(PlayerSystem())
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(SteeringSystem())
//        engine.addSystem(CollisionSystem(game))
        engine.addSystem(HealthSystem())
        engine.addSystem(SniperSystem(0.2f, entityBuilder))
        engine.addSystem(BulletSystem())
//        engine.addSystem(SpawnSystem(2f, entityBuilder, playerEntity))
        engine.addSystem(DecaySystem(0.1f))
        engine.addSystem(RegenerationSystem(0.5f))
//        engine.addSystem(SlowSystem())
        engine.addSystem(AgentSystem())
        engine.addSystem(DamageLabelSystem(camera))
//        engine.addSystem(ClickableSystem(camera))
        engine.addSystem(JumperSystem())
    }

    private fun gameOver() {
        println("------------------------------------")
        println("-------------Game Over--------------")
        println("------------------------------------")
        game.screen = MenuScreen(game)
    }
}
