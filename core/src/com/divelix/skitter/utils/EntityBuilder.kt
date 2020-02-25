package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.box2d.body
import kotlin.experimental.or

class EntityBuilder(val engine: PooledEngine,
                    val world: World,
                    private val assets: Assets) {

    fun createPlayer(x: Float, y: Float): Entity {
        val entityType = TypeComponent.PLAYER
        return engine.entity {
            with<PlayerComponent>()
            with<TypeComponent> { type = entityType }
            with<HealthComponent> { health = Data.playerData.ship.health }
//            with<RegenerationComponent> { amount = 1f }
            with<TransformComponent> {
                position.set(x, y, 1f)
                size.set(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.BUCKET_ICON)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = Constants.PLAYER_SIZE / 2f) {
                        density = 1f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.AGENT or TypeComponent.ENEMY_BULLET or TypeComponent.OBSTACLE or TypeComponent.SPAWN or TypeComponent.PUDDLE or TypeComponent.AGENT_SENSOR or TypeComponent.DOOR
//                    filter.groupIndex = -1
//                    isSensor = true
                    }
                    fixedRotation = true
                    position.set(x, y)
                    userData = this@entity.entity
                }
            }
        }
    }

    fun createCamera(playerEntity: Entity): OrthographicCamera {
        val camEntity = engine.entity {
            val playerPos = playerEntity.getComponent(TransformComponent::class.java).position
            with<CameraComponent> {
                camera.run {
                    setToOrtho(false, Constants.WIDTH, Constants.WIDTH * Gdx.graphics.height / Gdx.graphics.width)
                    position.set(playerPos)
                    update()
                }
            }
            with<BindComponent> { entity = playerEntity }
        }
        return camEntity.getComponent(CameraComponent::class.java).camera
    }

    fun createBullet(sourceEntity: Entity, aim: Vector2) {
        val sourceType = sourceEntity.getComponent(TypeComponent::class.java).type // TODO mb use ComponentMapper
        val entityType = if (sourceType == TypeComponent.PLAYER) TypeComponent.PLAYER_BULLET else TypeComponent.ENEMY_BULLET
        val initPos = sourceEntity.getComponent(B2dBodyComponent::class.java).body.position
        val initVelocity = sourceEntity.getComponent(B2dBodyComponent::class.java).body.linearVelocity
        val dirVec = aim.sub(initPos)
        val dirAngle = dirVec.angle() - 90f
        val width = 0.2f
        val height = 1f
        val speed = Data.playerData.gun.bulletSpeed
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<BulletComponent>()
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 0f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.BULLET_DEFAULT)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    box(width = width, height = height) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.PLAYER or TypeComponent.AGENT or TypeComponent.OBSTACLE
//                filter.groupIndex = 1
                        isSensor = true // TODO Carefully
                    }
                    position.set(initPos)
                    bullet = true
                    userData = (this@entity).entity
                    val velocity = Vector2(0f, 1f).scl(speed).rotate(dirAngle)
                    velocity.add(initVelocity)
                    linearVelocity.set(velocity)
                    angle = velocity.angleRad() - MathUtils.PI/2
                }
            }
        }
    }

    fun createLover(x: Float, y: Float, playerEntity: Entity) {
        val entityType = TypeComponent.AGENT
        engine.entity {
            with<LoverComponent>()
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { damage = Data.loverData.damage }
            with<HealthComponent> { health = Data.loverData.health }
            with<HealthBarComponent> { maxValue = Data.loverData.health }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(0.9f, 1.5f)
                origin.set(size).scl(0.5f)
            }
            with<SteerComponent> {
                maxSpeed = Data.loverData.maxSpeed
                maxForce = Data.loverData.maxForce
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.LOVER)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    polygon(Vector2(0f, 0.75f), Vector2(-0.45f, -0.75f), Vector2(0.45f, -0.75f)) {
                        density = 1f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                    }
                    angularDamping = 15f
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
            with<BindComponent> { entity = playerEntity }
//            with<ClickableComponent> { circle.set(x, y, entitySize/2)} // TODO maybe return later for new mechanics
        }
        LevelGenerator.enemiesCount++
    }

    fun createAgent(x: Float, y: Float): Entity {
        val entityType = TypeComponent.AGENT
        return engine.entity {
            with<AgentComponent>()
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { damage = 10f }
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(0.9f, 1.5f)
                origin.set(size).scl(0.5f)
            }
            with<SteerComponent> {
                steeringPoint.set(0f, 0.75f) // just front vertex from body component
                maxSpeed = 20f
                maxForce = 20f
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.LOVER)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    polygon(Vector2(0f, 0.75f), Vector2(-0.45f, -0.75f), Vector2(0.45f, -0.75f)) {
                        density = 1f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                    }
                    circle(7f, Vector2(0f, 0f)) {
                        isSensor = true
                        filter.categoryBits = TypeComponent.AGENT_SENSOR
                        filter.maskBits = TypeComponent.PLAYER or TypeComponent.AGENT or TypeComponent.OBSTACLE
                    }
                    linearDamping = 1f
                    angularDamping = 30f
                    position.set(x, y)
                    userData = (this@entity).entity
                }//.apply { println(mass) }
            }
            with<DamageLabelComponent>()
            LevelGenerator.enemiesCount++
        }
    }

    fun createJumper(x: Float, y: Float) {
        val entityType = TypeComponent.AGENT
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<JumperComponent>()
            with<EnemyComponent> { damage = 10f }
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(1f, 1f)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.SNIPER)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(0.5f, Vector2(0f, 0f)) {
                        filter.categoryBits = TypeComponent.AGENT
//                        filter.maskBits = TypeComponent.PLAYER or TypeComponent.AGENT or TypeComponent.OBSTACLE
                    }
                    linearDamping = 10f
                    position.set(x, y)
                    userData = (this@entity).entity
                }//.apply { println(mass) }
            }
            with<DamageLabelComponent>()
            LevelGenerator.enemiesCount++
        }
    }

    fun createSniper(x: Float, y: Float, playerEntity: Entity) {
        val entityType = TypeComponent.AGENT
        val entitySize = 1.5f
        val sniperDamage = 10f
        val sniperHealth = 50f
        engine.entity {
            with<SniperComponent>()
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { damage = sniperDamage }
            with<HealthComponent> { health = sniperHealth }
            with<HealthBarComponent> { maxValue = sniperHealth }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.SNIPER)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.KinematicBody) {
                    circle(radius = entitySize / 2f) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.PLAYER or TypeComponent.PLAYER_BULLET
//                        filter.groupIndex = 1
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
            with<BindComponent> { entity = playerEntity }
        }
        LevelGenerator.enemiesCount++
    }

    fun createRectObstacle(x: Float, y: Float, width: Float, height: Float) {
        val entityType = TypeComponent.OBSTACLE
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> {
                val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                    setColor(0.5f, 0.5f, 0.5f, 1f)
                    fill()
                }
                region = TextureRegion(Texture(pixel))
            }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.StaticBody) {
                    box(width = width, height = height) {
                        density = 10f
                        friction = 0f
                        restitution = 0f
                        filter.categoryBits = entityType
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
        }
    }

    fun createBreakableObstacle(x: Float, y: Float, width: Float = 2f, height: Float = 2f) {
        val entityType = TypeComponent.OBSTACLE
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> {
                val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                    setColor(0.7f, 0.7f, 0.1f, 1f)
                    fill()
                }
                region = TextureRegion(Texture(pixel))
            }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    box(width = width, height = height) {
                        density = 10f
                        friction = 0f
                        restitution = 0f
                        filter.categoryBits = entityType
                    }
                    linearDamping = 10f
                    angularDamping = 50f
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<DamageLabelComponent>()
        }
    }

    fun createCircleObstacle(x: Float, y: Float, radius: Float) {
        val entityType = TypeComponent.OBSTACLE
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(radius * 2f, radius * 2f)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.WHITE_CIRCLE)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.StaticBody) {
                    circle(radius = radius) {
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
//            with<CollisionComponent>()
        }
    }

    fun createWall(point1: Vector2, point2: Vector2): Entity {
        val entityType = TypeComponent.OBSTACLE
        return engine.entity {
            with<TypeComponent> { type = entityType }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.StaticBody) {
                    edge(point1, point2) {
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                    }
                    userData = (this@entity).entity
                }
            }
//            with<CollisionComponent>()
        }
    }

    fun createBg(x: Float, y: Float, width: Float, height: Float): Entity {
        val scale = 25
        return engine.entity {
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> {
                val bg = assets.manager.get<Texture>(Constants.BACKGROUND_IMAGE)
                bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
                val bgReg = TextureRegion(bg)
                bgReg.setRegion(0, 0, width.toInt() * scale, height.toInt() * scale)
                region = bgReg
            }
        }
    }

    fun createDoor(x: Float, y: Float, width: Float = 4f, height: Float = 1f): Entity {
        val entityType = TypeComponent.DOOR
        return engine.entity {
            with<TypeComponent> { type = entityType }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> {
                val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
                    setColor(1f, 0.0f, 0.8f, 1f)
                    fill()
                }
                region = TextureRegion(Texture(pixel))
            }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.StaticBody) {
                    box(width = width, height = height) {
                        density = 10f
                        friction = 0f
                        restitution = 0f
                        filter.categoryBits = entityType
                        isSensor = true
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
        }
    }

    fun createSpawn(x: Float, y: Float, radius: Float) {
        val entityType = TypeComponent.SPAWN
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<SpawnComponent> { circle.set(x, y, radius) }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(radius * 2f, radius * 2f)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.AIM)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.StaticBody) {
                    circle(radius) {
                        density = 10f
                        friction = 0f
                        restitution = 0f
                        filter.categoryBits = entityType
//                        filter.maskBits = TypeComponent.PLAYER
//                        filter.groupIndex = 1
                        isSensor = true
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
        }
    }

    fun createPuddle(x: Float, y: Float, radius: Float) {
        val entityType = TypeComponent.PUDDLE
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(radius * 2f, radius * 2f)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.WHITE_CIRCLE)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.StaticBody) {
                    circle(radius) {
                        density = 10f
                        friction = 0f
                        restitution = 0f
                        filter.categoryBits = entityType
//                        filter.maskBits = TypeComponent.PLAYER
//                        filter.groupIndex = 1
                        isSensor = true
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
        }
    }
}