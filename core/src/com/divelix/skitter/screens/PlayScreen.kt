package com.divelix.skitter.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.divelix.skitter.*
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.EntityBuilder
import ktx.app.KtxScreen
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.assets.toLocalFile
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
    private val levelEntities = Array<Entity>()

    init {
        Data.renderTime = 0f
        Data.physicsTime = 0f
        Data.score = 0
        Data.enemiesCount = 0
        Data.dirVec.set(0f, 0.000001f)// little init movement fixes 90deg ship rotation on init
        isPaused = false

        loadPlayerData()

        makeEnvironment()
        playerEntity = entityBuilder.createPlayer(5f, 2f)
        camera = entityBuilder.createCamera(playerEntity)
        hud = Hud(game, camera, entityBuilder, playerEntity)
//        makeEnemies()

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
                    Input.Keys.R -> {
                        val cmBody = mapperFor<B2dBodyComponent>()
                        levelEntities.forEach {
                            if (it.has(cmBody)) world.destroyBody(cmBody.get(it).body)
                            engine.removeEntity(it)
                        }
                        val b = playerEntity.getComponent(B2dBodyComponent::class.java).body
                        b.setTransform(5f, 2f, 0f)
                        Data.dirVec.set(0f, 0.000001f)
                    }
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, hud.hudStage, hud.playerCtrl)
        Gdx.input.inputProcessor = multiplexer
        world.setContactListener(B2dContactListener(game, camera, hud))
    }

    override fun render(delta: Float) {
        if (!isPaused) {
            clearDeadBodies()
            if (health <= 0f) gameOver()
        }
        engine.update(delta)
        debugRenderer.render(world, camera.combined)
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

    private fun makeEnvironment() {
        makeBattleground(0f, 0f, 10f, 20f)
//        entityBuilder.createBreakableObstacle(1f, 10f)
//        entityBuilder.createBreakableObstacle(0f, 10f)
//        entityBuilder.createBreakableObstacle(-1f, 10f)
        entityBuilder.createDoor(5f, 19.5f)

//        entityBuilder.createCircleObstacle(10f, 20f, 3f)
//        entityBuilder.createRectObstacle(-5f, 0f, 3f, 10f)
//        entityBuilder.createRectObstacle(-5f, 15f, 3f, 10f)
//        entityBuilder.createRectObstacle(-5f, 30f, 3f, 10f)
//        entityBuilder.createRectObstacle(5f, 0f, 3f, 10f)
//        entityBuilder.createRectObstacle(5f, 15f, 3f, 10f)
//        entityBuilder.createRectObstacle(5f, 30f, 3f, 10f)
//        entityBuilder.createPuddle(0f, 55f, 2f)
//        entityBuilder.createSpawn(0f, 10f, 2f)
    }

    fun makeBattleground(x: Float, y: Float, width: Float, height: Float) {
        levelEntities.add(entityBuilder.createBg(x + width / 2f, y + height / 2f, width, height))
        levelEntities.add(entityBuilder.createWall(Vector2(x, y), Vector2(x, y + height)))
        levelEntities.add(entityBuilder.createWall(Vector2(x, y + height), Vector2(x + width, y + height)))
        levelEntities.add(entityBuilder.createWall(Vector2(x + width, y + height), Vector2(x + width, y)))
        levelEntities.add(entityBuilder.createWall(Vector2(x + width, y), Vector2(x, y)))
    }

    private fun makeEnemies() {
        entityBuilder.createAgent(0f, 10f)
//        entityBuilder.createLover(-5f, -5f, playerEntity)
//        entityBuilder.createSniper(5f, 25f, playerEntity)
    }

    private fun createEngineSystems() {
        engine.addSystem(CameraSystem())
        engine.addSystem(PhysicsSystem(world, blackList))
        engine.addSystem(PlayerSystem())
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(SteeringSystem())
//        engine.addSystem(CollisionSystem(game))
        engine.addSystem(HealthSystem())
//        engine.addSystem(LoverSystem())
//        engine.addSystem(SniperSystem(1f, entityBuilder))
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
