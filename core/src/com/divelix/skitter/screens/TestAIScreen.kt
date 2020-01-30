package com.divelix.skitter.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.ObjectSet
import com.divelix.skitter.*
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.systems.*
import com.divelix.skitter.utils.EntityBuilder
import ktx.app.KtxScreen
import ktx.ashley.mapperFor
import ktx.log.info
import java.util.*

class TestAIScreen(val game: Main): KtxScreen {
    companion object {
        const val D_WIDTH = 1200
        const val D_HEIGHT = 700
        const val WIDTH = 50f
        const val HEIGHT = WIDTH * D_HEIGHT / D_WIDTH
        var slowRate = Constants.DEFAULT_SLOW_RATE
        var isPaused = false
    }
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    private val world = World(Vector2(0f, 0f), true).apply { setContactListener(B2dContactListener()) }
    private val debugRenderer = Box2DDebugRenderer()
    private val engine = PooledEngine()
    private val entityBuilder = EntityBuilder(engine, world, assets)
    private val camera = OrthographicCamera()
    private val blackList = ArrayList<Body>() // list of bodies to kill

    private val touchPos = Vector3()
    private val agents = ObjectSet<Entity>()

    init {
        isPaused = false

//        makeEnvironment()
//        makeEnemies()

        createEngineSystems()

        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.SPACE -> isPaused = !isPaused
                    Input.Keys.B -> println(world.bodyCount)
                    Input.Keys.Z -> entityBuilder.createAgent(0f, 10f)
                    Input.Keys.BACKSPACE -> {
                        val cmBody = mapperFor<B2dBodyComponent>()
                        for (agent in agents) cmBody.get(agent).isDead = true
                        agents.clear()
                    }
                }
                return true
            }
        }
        Gdx.input.inputProcessor = handler
    }

    override fun render(delta: Float) {
        if (!isPaused) {
            clearDeadBodies()
        }
        engine.update(delta)
        debugRenderer.render(world, camera.combined)
        controlCamera()
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
        camera.setToOrtho(false, WIDTH, HEIGHT)
    }

    override fun hide() {
        info("PlayScreen") { "hide()" }
    }

    override fun dispose() {
        info("PlayScreen") { "dispose()" }
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

    private fun makeEnvironment() {
        entityBuilder.createWall(Vector2(-8f, -8f), Vector2(-8f, 50f))
        entityBuilder.createWall(Vector2(-8f, 50f), Vector2(50f, 50f))
        entityBuilder.createWall(Vector2(50f, 50f), Vector2(50f, -8f))
        entityBuilder.createWall(Vector2(50f, -8f), Vector2(-8f, -8f))
        entityBuilder.createCircleObstacle(10f, 20f, 3f)
    }

    private fun makeEnemies() {
        entityBuilder.createAgent(0f, 10f)
    }

    private fun createEngineSystems() {
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(PhysicsSystem(world, blackList))
        engine.addSystem(CollisionSystem(game))
        engine.addSystem(EnemySystem())
        engine.addSystem(BulletSystem())
        engine.addSystem(SteeringSystem())
        engine.addSystem(AgentSystem())
    }

    private fun controlCamera() {
        val camShift = 0.5f
        val zoomDelta = 0.05f
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.position.x -= camShift
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.position.y += camShift
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) camera.position.x += camShift
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y -= camShift
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1)) camera.zoom += zoomDelta
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) camera.zoom -= zoomDelta
        camera.update()
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touchPos)
            val agent = entityBuilder.createAgent(touchPos.x, touchPos.y)
            agents.add(agent)
            val cmBody = mapperFor<B2dBodyComponent>()
//            cmBody.get(agent).body.applyForceToCenter(100f, 0f, true)
        }
    }
}
