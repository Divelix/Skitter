package com.divelix.skitter.screens.experimental

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.ObjectSet
import com.divelix.skitter.*
import com.divelix.skitter.data.Assets
import com.divelix.skitter.gameplay.GameEngine
import com.divelix.skitter.gameplay.components.B2dBodyComponent
import com.divelix.skitter.gameplay.systems.BehaviorSystem
import com.divelix.skitter.gameplay.systems.BulletSystem
import com.divelix.skitter.gameplay.systems.PhysicsSystem
import com.divelix.skitter.gameplay.systems.RenderingSystem
import com.divelix.skitter.gameplay.EntityBuilder
import ktx.app.KtxScreen
import ktx.ashley.mapperFor
import ktx.log.info
import java.util.*

class TestAIScreen(val game: Main): KtxScreen {
    companion object {
        const val D_WIDTH = 700
        const val D_HEIGHT = 700
        const val WIDTH = 80f
        const val HEIGHT = WIDTH * D_HEIGHT / D_WIDTH
    }
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    private val world = World(Vector2(0f, 0f), true)
    private val debugRenderer = Box2DDebugRenderer(true, true, false, true, true, true)
    private val engine = PooledEngine()
    private val entityBuilder = EntityBuilder(engine, world, assets)
    private val camera = OrthographicCamera()
    private val blackList = ArrayList<Body>() // list of bodies to kill

    private val touchPos = Vector3()
    private val agents = ObjectSet<Entity>()

    init {
        GameEngine.isPaused = false
        makeEnvironment()
//        makeEnemies()

        createEngineSystems()

        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                val cmBody = mapperFor<B2dBodyComponent>()
                when(keycode) {
                    Input.Keys.SPACE -> GameEngine.isPaused = !GameEngine.isPaused
                    Input.Keys.B -> println(world.bodyCount)
                    Input.Keys.S -> println("agents.size = ${agents.size}")
                    Input.Keys.F -> println("FPS = ${Gdx.graphics.framesPerSecond}")
                    Input.Keys.Z -> entityBuilder.createAgent(0f, 10f)
//                    Input.Keys.BACKSPACE -> {
//                        for (agent in agents) cmBody.get(agent).isDead = true
//                        agents.clear()
//                    }
                    Input.Keys.R -> {
                        val f = 500f
                        for (agent in agents) cmBody.get(agent).body.applyForceToCenter(MathUtils.random(-f, f), MathUtils.random(-f, f), true)
                    }
                }
                return true
            }
        }
        Gdx.input.inputProcessor = handler
//        world.setContactListener(B2dContactListener(game, camera))
    }

    override fun render(delta: Float) {
        if (!GameEngine.isPaused) {
            clearDeadBodies()
        }
        engine.update(delta)
        debugRenderer.render(world, camera.combined)
        controlCamera()
    }

    override fun pause() {
        info("TestAIScreen") { "pause()" }
        GameEngine.isPaused = true
    }

    override fun resume() {
        info("TestAIScreen") { "resume()" }
        GameEngine.isPaused = false
    }

    override fun resize(width: Int, height: Int) {
        info("TestAIScreen") { "resize()" }
        camera.setToOrtho(false, WIDTH, HEIGHT)
    }

    override fun hide() {
        info("TestAIScreen") { "hide()" }
    }

    override fun dispose() {
        info("TestAIScreen") { "dispose()" }
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
//        entityBuilder.createCircleObstacle(10f, 20f, 3f)
//        entityBuilder.createRectObstacle(10f, 20f, 7f, 8f)
        entityBuilder.createWall(Vector2(10f, 0f), Vector2(10f, 20f))
        entityBuilder.createWall(Vector2(15f, 0f), Vector2(15f, 20f))
    }

    private fun makeEnemies() {
        entityBuilder.createAgent(0f, 10f)
    }

    private fun createEngineSystems() {
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(PhysicsSystem(world))
//        engine.addSystem(HealthSystem())
        engine.addSystem(BulletSystem())
        engine.addSystem(BehaviorSystem())
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
        }
    }
}
