package com.divelix.skitter

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
    val playerEntity by lazy { entityBuilder.createPlayer(5f, 2f, playCam) }
    val hud by lazy { Hud(game, entityBuilder, playerEntity) }

    init {
        isPaused = false
        createEngineSystems()
        world.setContactListener(B2dContactListener(game, engine, hud))
    }

    fun update(delta: Float) {
        if (!isPaused) {
            if (PlayScreen.health <= 0f) isPaused = true
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
        engine.addSystem(HealthSystem())
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

        val cmCamera = mapperFor<CameraComponent>()
        val cmPlayer = mapperFor<PlayerComponent>()
        val cmBody = mapperFor<B2dBodyComponent>()
        val cmType = mapperFor<TypeComponent>()
        val cmBind = mapperFor<BindComponent>()
        val cmBullet = mapperFor<BulletComponent>()
        val cmClick = mapperFor<ClickableComponent>()
        val cmDmgLabel = mapperFor<DamageLabelComponent>()
        val cmDecay = mapperFor<DecayComponent>()
        val cmEnemy = mapperFor<EnemyComponent>()
        val cmHealthBar = mapperFor<HealthBarComponent>()
        val cmHealth = mapperFor<HealthComponent>()
        val cmRegen = mapperFor<RegenerationComponent>()
        val cmSlow = mapperFor<SlowComponent>()
        val cmSpawn = mapperFor<SpawnComponent>()
        val cmSteer = mapperFor<SteerComponent>()
        val cmVision = mapperFor<VisionComponent>()
        val cmTexture = mapperFor<TextureComponent>()
        val cmTransform = mapperFor<TransformComponent>()

        val cmJumper = mapperFor<JumperComponent>()
        val cmRadial = mapperFor<RadialComponent>()
        val cmSniper = mapperFor<SniperComponent>()
        val cmWomb = mapperFor<WombComponent>()
    }
}