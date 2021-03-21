package com.divelix.skitter.gameplay

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
import com.divelix.skitter.data.*
import com.divelix.skitter.gameplay.components.*
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.box2d.*
import ktx.collections.*
import ktx.log.info
import ktx.scene2d.Scene2DSkin
import ktx.style.get

class EntityBuilder(private val activePlayerData: ActivePlayerData,
                    private val engine: PooledEngine,
                    private val world: World) {

    fun createPlayer(x: Float, y: Float): Entity {
        val entityType = TypeComponent.PLAYER
        return engine.entity {
            with<PlayerComponent>()
            with<TypeComponent> { type = entityType }
            with<HealthComponent> { health = activePlayerData.shipHealth }
//            with<RegenerationComponent> { amount = 1f }
            with<TransformComponent> {
                position.set(x, y, 2f)
                size.set(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.SHIP_DEFAULT())) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = Constants.PLAYER_SIZE / 2f) {
                        density = 1f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.PLAYER_MB
                    }
                    linearDamping = 10f
                    fixedRotation = true
                    position.set(x, y)
                    userData = this@entity.entity
                }
            }
            with<DamageLabelComponent>()
        }
    }

    fun createCamera(playCam: OrthographicCamera, playerEntity: Entity?): Entity {
        return engine.entity {
            with<CameraComponent> {
                camera = playCam
                camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_WIDTH * Gdx.graphics.height / Gdx.graphics.width)
            }
            if (playerEntity != null) with<BindComponent> { entity = playerEntity }
        }
    }

    fun createPlayerBullet(sourceEntity: Entity, aim: Vector2) {
        val entityType = TypeComponent.PLAYER_BULLET
        val sourceBody = try {
            sourceEntity[B2dBodyComponent.mapper]!!.body
        } catch (e: NullPointerException) {
            info(TAG) { "Can't shoot - player is dead" }
            return
        }
        val initPos = sourceBody.position
        val initVelocity = sourceBody.linearVelocity
        val dirVec = aim.sub(initPos)
        val dirAngle = dirVec.angleDeg() - 90f
        val width = 0.2f
        val height = 1f
        val speed = activePlayerData.gunSpeed
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<BulletComponent> {
                damage = activePlayerData.gunDamage
            }
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 1f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.BULLET_DEFAULT()).texture) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    box(width = width, height = height) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.PLAYER_BULLET_MB
//                        isSensor = true
                    }
                    position.set(initPos)
                    bullet = true
                    userData = (this@entity).entity
                    val velocity = Vector2(0f, 1f).scl(speed).rotateDeg(dirAngle)
                    velocity.add(initVelocity)
                    linearVelocity.set(velocity)
                    angle = velocity.angleRad() - MathUtils.PI / 2
                }
            }
        }
    }

    fun createEnemyBullet(sourceEntity: Entity, aim: Vector2) {
        val entityType = TypeComponent.ENEMY_BULLET
        val initPos = sourceEntity.getComponent(B2dBodyComponent::class.java).body.position
        val initVelocity = sourceEntity.getComponent(B2dBodyComponent::class.java).body.linearVelocity
        val dirVec = aim.sub(initPos)
        val dirAngle = dirVec.angleDeg() - 90f
        val width = 0.5f
        val height = 0.5f
        val speed = 5f
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<BulletComponent> {
                damage = 10f // TODO load enemy damage from json
            }
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 0f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.BULLET_DEFAULT()).texture) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    box(width = width, height = height) {
                        density = 50f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.ENEMY_BULLET_MB
//                        isSensor = true
                    }
                    position.set(initPos)
                    bullet = true
                    userData = (this@entity).entity
                    val velocity = Vector2(0f, 1f).scl(speed).rotateDeg(dirAngle)
                    velocity.add(initVelocity)
                    linearVelocity.set(velocity)
                    angle = velocity.angleRad() - MathUtils.PI / 2f
                }
            }
        }
    }

    fun createAgent(x: Float, y: Float): Entity {
        val entityType = TypeComponent.ENEMY
        val enemyType = Enemy.AGENT
        return engine.entity {
            with<VisionComponent>()
            with<SteerComponent> {
                steeringPoint.set(0f, 0.75f) // just front vertex from body component
                maxSpeed = 20f
                maxForce = 20f
                finalForce = 20f
                behaviors + arrayOf(
                        Behavior.WANDER,
                        Behavior.SEEK
                )
            }
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { type = enemyType }
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(0.9f, 1.5f)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.AGENT())) }
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
                        filter.categoryBits = TypeComponent.VISION_SENSOR
                        filter.maskBits = TypeComponent.VISION_SENSOR_MB
                    }
                    linearDamping = 1f
                    angularDamping = 30f
                    position.set(x, y)
                    userData = (this@entity).entity
                }//.apply { println(mass) }
            }
            with<DamageLabelComponent>()
            LevelManager.enemiesCount++
        }
    }

    fun createJumper(x: Float, y: Float) {
        val entityType = TypeComponent.ENEMY
        val enemyType = Enemy.JUMPER
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<JumperComponent>()
            with<EnemyComponent> { type = enemyType }
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(1f, 1f)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.JUMPER())) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(0.5f, Vector2(0f, 0f)) {
                        filter.categoryBits = TypeComponent.ENEMY
                        filter.maskBits = TypeComponent.ENEMY_MB
                    }
                    linearDamping = 10f
                    position.set(x, y)
                    userData = (this@entity).entity
                }//.apply { println(mass) }
            }
            with<DamageLabelComponent>()
            LevelManager.enemiesCount++
        }
    }

    fun createSniper(x: Float, y: Float) {
        val entityType = TypeComponent.ENEMY
        val enemyType = Enemy.SNIPER
        val entitySize = 1.5f
//        val sniperDamage = 10f
        val sniperHealth = 200f
        engine.entity {
            with<SniperComponent>()
            with<TypeComponent> { type = entityType }
            with<VisionComponent>()
            with<EnemyComponent> { type = enemyType }
            with<TowerComponent> {
                sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.SNIPER_TOWER()))
            }
            with<HealthComponent> { health = sniperHealth }
            with<HealthBarComponent> { maxValue = sniperHealth }
            with<TransformComponent> {
                position.set(x, y, 1f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.SNIPER_BASE())) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = entitySize / 2f) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.ENEMY_MB
                    }
                    circle(10f, Vector2(0f, 0f)) {
                        isSensor = true
                        filter.categoryBits = TypeComponent.VISION_SENSOR
                        filter.maskBits = TypeComponent.VISION_SENSOR_MB
                    }
                    linearDamping = 5f
                    angularDamping = 5f
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
//            with<BindComponent> { entity = playerEntity }
            with<DamageLabelComponent>()
            LevelManager.enemiesCount++
        }
    }

    fun createWomb(x: Float, y: Float) {
        val entityType = TypeComponent.ENEMY
        val enemyType = Enemy.WOMB
        val entitySize = 2f
        val wombHealth = 300f
        engine.entity {
            with<WombComponent>()
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { type = enemyType }
            with<HealthComponent> { health = wombHealth }
            with<HealthBarComponent> { maxValue = wombHealth }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.WOMB())) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = entitySize / 2f) {
                        density = 20f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.ENEMY_MB
                    }
                    linearDamping = 10f
                    angularDamping = 10f
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<DamageLabelComponent>()
            LevelManager.enemiesCount++
        }
    }

    fun createKid(womb: Entity) {
        val entityType = TypeComponent.ENEMY
        val enemyType = Enemy.KID
        val entitySize = 0.5f
        val initPos = womb[B2dBodyComponent.mapper]!!.body.position.apply {
            x += MathUtils.random(-1f, 1f)
            y += MathUtils.random(-1f, 1f)
        }
        engine.entity {
            with<VisionComponent>()
            with<SteerComponent> {
                maxSpeed = 10f
                maxForce = 10f
                finalForce = 5f
//                behaviors + arrayOf(
//                        Behavior.WANDER,
//                        Behavior.FLEE,
//                        Behavior.SEPARATION,
//                        Behavior.ALIGNMENT,
//                        Behavior.COHESION,
//                        Behavior.OBSTACLE_AVOIDANCE)
            }
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { type = enemyType }
            with<HealthComponent> {
                isIntHp = true
                health = 1f
            }
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 0f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.KID())) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = entitySize / 2f) {
                        density = 1f
                        filter.categoryBits = entityType
//                        filter.maskBits = TypeComponent.ENEMY_MB
                    }
                    circle(3f, Vector2(0f, 0f)) {
                        isSensor = true
                        filter.categoryBits = TypeComponent.VISION_SENSOR
                        filter.maskBits = TypeComponent.VISION_SENSOR_MB
                    }
                    linearDamping = 1f
                    angularDamping = 30f
                    position.set(initPos)
                    userData = (this@entity).entity
                }
            }
            with<DamageLabelComponent>()
            LevelManager.enemiesCount++
        }
    }

    fun createRadial(x: Float, y: Float) {
        val entityType = TypeComponent.ENEMY
        val enemyType = Enemy.RADIAL
        val texture = Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.RADIAL())
        val textureWidth = 2f
        val ratio = texture.regionHeight.toFloat() / texture.regionWidth
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { type = enemyType }
            with<RadialComponent>()
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 2f)
                size.set(textureWidth, textureWidth * ratio)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { sprite.setRegion(texture) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = textureWidth / 2f) {
                        density = 10f
                        filter.categoryBits = entityType
//                        filter.maskBits = TypeComponent.ENEMY_MB
                    }
                    linearDamping = 1f
//                    angularDamping = 30f
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<DamageLabelComponent>()
            LevelManager.enemiesCount++
        }
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
                sprite.setRegion(Scene2DSkin.defaultSkin.get<Texture>(Constants.LIGHT_GRAY_PIXEL))
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
            with<HealthComponent> {
                isIntHp = true
                health = 5f
            }
            with<HealthBarComponent> { maxValue = 5f }
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
                sprite.setRegion(Texture(pixel))
            }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.KinematicBody) {
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
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.WHITE_CIRCLE())) }
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

    fun createWall(point1: Vector2, point2: Vector2) {
        val entityType = TypeComponent.OBSTACLE
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<TransformComponent> {// needed to be seen by PhysicsSystem (as should go to blackList)
                position.set(point1.x, point1.y, 1f)
                size.set(1f, 1f)
                origin.set(size).scl(0.5f)
            }
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
        }
    }

    fun createBg(x: Float, y: Float, width: Float, height: Float) {
        engine.entity {
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TileComponent> {
                tile.region = Scene2DSkin.defaultSkin[RegionName.DARK_HONEYCOMB()]
                tile.scale = 0.04f
            }
        }
    }

    fun createDoor(x: Float, y: Float, width: Float = 4f, height: Float = 1f) {
        val entityType = TypeComponent.DOOR
        engine.entity {
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
                sprite.setRegion(Texture(pixel))
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
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.AIM())) }
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
            with<TextureComponent> { sprite.setRegion(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.WHITE_CIRCLE())) }
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
        }
    }

    companion object {
        const val TAG = "EntityBuilder"
    }
}