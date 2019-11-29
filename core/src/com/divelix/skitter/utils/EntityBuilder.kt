package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.JsonReader
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import ktx.ashley.entity
import ktx.assets.toLocalFile
import ktx.box2d.body
import ktx.box2d.mouseJointWith
import kotlin.experimental.or

class EntityBuilder(private val engine: PooledEngine, private val world: World, private val assets: Assets) {

    fun createPlayer(hp: Float): Entity {
        val entityType = TypeComponent.PLAYER
        return engine.entity {
            with<TypeComponent> { type = entityType }
            with<PlayerComponent>()
            with<HealthComponent> { health = hp }
            with<TransformComponent> {
                position.set(0f, 0f, 1f)
                size.set(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE)
                origin.set(size).scl(0.5f)
            }
            with<MoveComponent> {
                speed = Data.playerData.ship.speed
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.PLAYER_DEFAULT)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = Constants.PLAYER_SIZE / 2f) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.ENEMY or TypeComponent.OBSTACLE or TypeComponent.SPAWN or TypeComponent.PUDDLE
//                    filter.groupIndex = -1
//                    isSensor = true
                    }
                    fixedRotation = true
                    position.set(0f, 0f)
                    userData = this@entity.entity
                }
            }
            with<CollisionComponent>()
        }
    }

    fun createCamera(playerEntity: Entity): OrthographicCamera {
        val camEntity = engine.entity {
            with<CameraComponent> { camera.position.set(0f, 0f, 1f) }
            with<BindComponent> { entity = playerEntity }
        }
        return camEntity.getComponent(CameraComponent::class.java).camera
    }

    fun createBullet(playerEntity: Entity, aim: Vector2) {
        val entityType = TypeComponent.BULLET
        val initPos = playerEntity.getComponent(B2dBodyComponent::class.java).body.position
        val initVelocity = playerEntity.getComponent(B2dBodyComponent::class.java).body.linearVelocity
        val dirVec = aim.sub(initPos)
        val angleInRad = dirVec.angleRad() - MathUtils.PI / 2f
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
                        filter.maskBits = TypeComponent.ENEMY or TypeComponent.OBSTACLE
//                filter.groupIndex = 1
                        isSensor = true // TODO Carefully
                    }
                    position.set(initPos)
                    angle = angleInRad
                    bullet = true
                    userData = (this@entity).entity
                    val velocity = Vector2(0f, 1f).scl(speed).rotateRad(angleInRad)
                    velocity.add(initVelocity)
                    linearVelocity.set(velocity)
                }
            }
            with<CollisionComponent>()
            with<BindComponent> { entity = playerEntity }
        }
    }

    fun createEnemy(x: Float, y: Float, entitySize: Float, playerEntity: Entity) {
        val entityType = TypeComponent.ENEMY
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<EnemyComponent> { damage = Data.loverData.damage }
            with<HealthComponent> { health = Data.loverData.health }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(entitySize, entitySize)
                origin.set(size).scl(0.5f)
            }
            with<MoveComponent> {
                speed = Data.loverData.speed
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.ENEMY_DEFAULT)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = entitySize / 2f) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
//                        filter.maskBits = TypeComponent.PLAYER or TypeComponent.BULLET or TypeComponent.ENEMY
//                        filter.groupIndex = 1
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
            with<HealthBarComponent> { maxValue = Data.loverData.health }
            with<BindComponent> { entity = playerEntity }
//            with<ClickableComponent> { circle.set(x, y, entitySize/2)} // TODO maybe return later for new mechanics
        }
        Data.enemiesCount++
    }

    fun createObstacle(x: Float, y: Float, width: Float, height: Float) {
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
                        filter.maskBits = TypeComponent.PLAYER or TypeComponent.ENEMY or TypeComponent.BULLET
//                        filter.groupIndex = 1
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
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

    fun createBattleground(x: Float, y: Float, width: Float, height: Float) {
        val entityType = TypeComponent.OBSTACLE
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(width, height)
                origin.setZero()
            }
            with<TextureComponent> {
                val texture = assets.manager.get<Texture>(Constants.BACKGROUND_IMAGE)
//                val texture = Texture("anti-seamless.png")
                val textureRegion = TextureRegion(texture)
                texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
                textureRegion.setRegion(0, 0, (width*Constants.PPM).toInt(), (height*Constants.PPM).toInt())
                region = textureRegion
            }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.StaticBody) {
                    val vertices = arrayOf(Vector2(0f, 0f),
                                           Vector2(0f, height),
                                           Vector2(width, height),
                                           Vector2(width, 0f))
                    loop(*vertices) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
//                        filter.maskBits = TypeComponent.PLAYER or TypeComponent.BULLET or TypeComponent.ENEMY
//                        filter.groupIndex = 1
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
        }
    }
}