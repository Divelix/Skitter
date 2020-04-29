package com.divelix.skitter

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.divelix.skitter.components.*
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.mapperFor

class GameEngine(val game: Main) {
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    private val world = World(Vector2(0f, 0f), true)
    private val debugRenderer = Box2DDebugRenderer()
    val engine = PooledEngine(20, 200, 50, 100)
    val entityBuilder = EntityBuilder(engine, world, assets)

    private val playCam = OrthographicCamera()
    val playerEntity by lazy { entityBuilder.createPlayer(5f, 2f, playCam) }
    val hud by lazy { Hud(game, entityBuilder, playerEntity) }

    init {
        createEngineSystems()
        world.setContactListener(B2dContactListener(game, engine, hud))
    }

    fun update(delta: Float) {
        if (!isPaused) {
            engine.update(delta)
            debugRenderer.render(world, playCam.combined)
        }
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