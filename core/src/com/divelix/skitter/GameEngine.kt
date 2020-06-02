package com.divelix.skitter

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.mapperFor
import ktx.graphics.use

class GameEngine(val game: Main) {
    private val context = game.getContext()
    private val assets = context.inject<Assets>()
    private val batch = context.inject<SpriteBatch>()

    private val world = World(Vector2(0f, 0f), true)
    private val debugRenderer = Box2DDebugRenderer()
    val engine = PooledEngine(20, 200, 50, 100)
    val entityBuilder = EntityBuilder(engine, world, assets)

    private val playCam = OrthographicCamera()
    val playerEntity by lazy { entityBuilder.createPlayer(5f, 2f) }
    val cameraEntity: Entity
    val hud by lazy { Hud(game, entityBuilder, playerEntity, playCam) }

    init {
        isPaused = false
        cameraEntity = entityBuilder.createCamera(playCam, playerEntity)
        createEngineSystems()
        world.setContactListener(B2dContactListener(game, engine, hud))
    }

    fun update(delta: Float) {
        if (!isPaused) {
//            if (PlayScreen.health <= 0f) isPaused = true
            engine.update(delta)
        }
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = playCam.combined
        batch.use {
            val t = assets.frameBuffer.colorBufferTexture
            t.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
            val bufferTexture = TextureRegion(t).apply { flip(false, true) }
            val w = Constants.WIDTH
            val h = Constants.WIDTH * Gdx.graphics.height / Gdx.graphics.width
            batch.draw(bufferTexture, playCam.position.x - w/2f, playCam.position.y - h/2f, w, h)
        }
        debugRenderer.render(world, playCam.combined)

        hud.update()
    }

    private fun createEngineSystems() {
        engine.addSystem(CameraSystem())
        engine.addSystem(PhysicsSystem(world))
        engine.addSystem(PlayerSystem())
        engine.addSystem(RenderingSystem(context, playCam))
        engine.addSystem(DamageLabelSystem(playCam)) // before HealthSystem to let label update init position on last hit
        engine.addSystem(HealthSystem(hud))
        engine.addSystem(SniperSystem(1.5f, entityBuilder))
        engine.addSystem(BulletSystem())
//        engine.addSystem(SpawnSystem(2f, entityBuilder, playerEntity))
        engine.addSystem(DecaySystem(0.1f))
//        engine.addSystem(RegenerationSystem(0.5f))
        engine.addSystem(SlowSystem())
        engine.addSystem(BehaviorSystem())
//        engine.addSystem(ClickableSystem(camera))
        engine.addSystem(JumperSystem())
        engine.addSystem(WombSystem(5f, entityBuilder))
        engine.addSystem(RadialSystem(2f, entityBuilder))
    }

    companion object {
        var slowRate = Constants.DEFAULT_SLOW_RATE
        var isPaused = false
    }
}