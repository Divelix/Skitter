package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
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
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.*
import ktx.ashley.entity
import ktx.box2d.body
import ktx.collections.*

class EntityBuilder(val engine: PooledEngine,
                    private val world: World,
                    private val assets: Assets) {

    fun createPlayer(x: Float, y: Float): Entity {
        val entityType = TypeComponent.PLAYER
        return engine.entity {
            with<PlayerComponent>()
            with<TypeComponent> { type = entityType }
            with<HealthComponent> { health = Data.playerData.ship.health }
//            with<RegenerationComponent> { amount = 1f }
            with<TransformComponent> {
                position.set(x, y, 2f)
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

    fun createCamera(playerEntity: Entity): Entity {
        return engine.entity {
            with<CameraComponent> {
                camera.setToOrtho(false, Constants.WIDTH, Constants.WIDTH * Gdx.graphics.height / Gdx.graphics.width)
            }
            with<BindComponent> { entity = playerEntity }
        }
    }

    fun createPlayerBullet(sourceEntity: Entity, aim: Vector2) {
        val entityType = TypeComponent.PLAYER_BULLET
        val sourceBody = GameEngine.cmBody.get(sourceEntity).body
        val initPos = sourceBody.position
        val initVelocity = sourceBody.linearVelocity
        val dirVec = aim.sub(initPos)
        val dirAngle = dirVec.angle() - 90f
        val width = 0.2f
        val height = 1f
        val speed = Data.playerData.gun.bulletSpeed
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<BulletComponent> {
                damage = Data.playerData.gun.damage
            }
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 1f)
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
                        filter.maskBits = TypeComponent.PLAYER_BULLET_MB
//                        isSensor = true
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

    fun createEnemyBullet(sourceEntity: Entity, aim: Vector2) {
        val entityType = TypeComponent.ENEMY_BULLET
        val initPos = sourceEntity.getComponent(B2dBodyComponent::class.java).body.position
        val initVelocity = sourceEntity.getComponent(B2dBodyComponent::class.java).body.linearVelocity
        val dirVec = aim.sub(initPos)
        val dirAngle = dirVec.angle() - 90f
        val width = 0.5f
        val height = 0.5f
        val speed = 10f
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<BulletComponent> {
                damage = 10f // TODO load enemy damage from json
            }
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 1f)
                size.set(width, height)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.BULLET_DEFAULT)) }
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
                    val velocity = Vector2(0f, 1f).scl(speed).rotate(dirAngle)
                    velocity.add(initVelocity)
                    linearVelocity.set(velocity)
                    angle = velocity.angleRad() - MathUtils.PI / 2f
                }
            }
        }
    }

    fun createAgent(x: Float, y: Float): Entity {
        val entityType = TypeComponent.ENEMY
        return engine.entity {
            with<VisionComponent>()
            with<SteerComponent> {
                steeringPoint.set(0f, 0.75f) // just front vertex from body component
                maxSpeed = 20f
                maxForce = 20f
                finalForce = 20f
                behaviors + arrayOf(
                        Behaviors.WANDER,
                        Behaviors.SEEK
                )
            }
            with<TypeComponent> { type = entityType }
            with<EnemyComponent>()// { damage = 10f }
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(0.9f, 1.5f)
                origin.set(size).scl(0.5f)
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
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<JumperComponent>()
            with<EnemyComponent>()// { damage = 10f }
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(1f, 1f)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.JUMPER)) }
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
        val entitySize = 1.5f
        val sniperDamage = 10f
        val sniperHealth = 200f
        engine.entity {
            with<SniperComponent>()
            with<TypeComponent> { type = entityType }
            with<VisionComponent>()
            with<EnemyComponent>()// { damage = sniperDamage }
            with<HealthComponent> { health = sniperHealth }
            with<HealthBarComponent> { maxValue = sniperHealth }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.SNIPER)) }
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
                    linearDamping = 10f
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
        val entitySize = 2f
        val wombHealth = 300f
        engine.entity {
            with<WombComponent>()
            with<TypeComponent> { type = entityType }
            with<EnemyComponent>()
            with<HealthComponent> { health = wombHealth }
            with<HealthBarComponent> { maxValue = wombHealth }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.WOMB)) }
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
        val entitySize = 0.5f
        val initPos = GameEngine.cmBody.get(womb).body.position.apply {
            x += MathUtils.random(-1f, 1f)
            y += MathUtils.random(-1f, 1f)
        }
        engine.entity {
            with<VisionComponent>()
            with<SteerComponent> {
                maxSpeed = 10f
                maxForce = 10f
                finalForce = 5f
                behaviors + arrayOf(
//                        Behaviors.WANDER,
                        Behaviors.FLEE,
                        Behaviors.SEPARATION,
                        Behaviors.ALIGNMENT,
                        Behaviors.COHESION,
                        Behaviors.OBSTACLE_AVOIDANCE)
            }
            with<TypeComponent> { type = entityType }
            with<EnemyComponent>()
            with<HealthComponent> {
                isIntHp = true
                health = 1f
            }
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 0f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.KID)) }
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
        val texture = assets.manager.get<Texture>(Constants.RADIAL)
        val textureWidth = 2f
        val ratio = texture.height.toFloat() / texture.width
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<EnemyComponent>()
            with<RadialComponent>()
            with<HealthComponent> { health = 100f }
            with<HealthBarComponent> { maxValue = 100f }
            with<TransformComponent> {
                position.set(x, y, 2f)
                size.set(textureWidth, textureWidth * ratio)
                origin.set(size).scl(0.5f)
            }
            with<TextureComponent> { region = TextureRegion(texture) }
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
                region = TextureRegion(Texture(pixel))
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
        val scale = 25
        engine.entity {
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
        }
    }
}