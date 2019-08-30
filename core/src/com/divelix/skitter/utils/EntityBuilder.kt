package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.components.*
import ktx.ashley.entity
import ktx.box2d.body
import ktx.box2d.mouseJointWith

class EntityBuilder(private val engine: PooledEngine, private val world: World, private val assets: Assets) {

    fun createPlayer(): Entity {

        val entityType = TypeComponent.PLAYER
        return engine.entity {
            with<TypeComponent> { type = entityType }
            with<PlayerComponent>()
            with<TransformComponent> {
                position.set(0f, 0f, 1f)
                size.set(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.PLAYER_DEFAULT)) }
            val cursorBody = world.body(type = BodyDef.BodyType.StaticBody) {
                position.set(0f, 0f)
            }
            val playerBody = world.body(type = BodyDef.BodyType.DynamicBody) {
                circle(radius = Constants.PLAYER_SIZE / 2f) {
                    density = 10f
                    friction = 0.5f
                    restitution = 0f
                    filter.categoryBits = entityType
                    filter.maskBits = TypeComponent.ENEMY
//                filter.groupIndex = -1
                    isSensor = true
                }
                fixedRotation = true
                position.set(0f, 0f)
                userData = this@entity.entity
            }
            with<B2dBodyComponent> { body = playerBody }
            with<MouseComponent> {
                mouseJoint = cursorBody.mouseJointWith(playerBody) {
                    maxForce = 1000000f
                    target.set(bodyB.position)
                    collideConnected = false
                    dampingRatio = 1f
//            frequencyHz = 100f
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
        val dirVec = aim.sub(initPos)
        val angleInRad = dirVec.angleRad() - MathUtils.PI / 2f
        val width = 0.1f
        val height = 0.5f
        val speed = Constants.BULLET_SPEED
        engine.entity {
            with<TypeComponent> { type = entityType }
            with<BulletComponent>()
            with<TransformComponent> {
                position.set(initPos.x, initPos.y, 0f)
                size.set(width, height)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.RIFLE)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    box(width = 0.1f, height = 0.5f) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
                        filter.maskBits = TypeComponent.ENEMY
//                filter.groupIndex = 1
                        isSensor = true // TODO Carefully
                    }
                    position.set(initPos)
                    angle = angleInRad
                    bullet = true
                    userData = (this@entity).entity
                    linearVelocity.set(Vector2(0f, 1f).scl(speed).rotateRad(angleInRad))
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
            with<EnemyComponent>()
            with<TransformComponent> {
                position.set(x, y, 0f)
                size.set(entitySize, entitySize)
            }
            with<TextureComponent> { region = TextureRegion(assets.manager.get<Texture>(Constants.ENEMY_DEFAULT)) }
            with<B2dBodyComponent> {
                body = world.body(type = BodyDef.BodyType.DynamicBody) {
                    circle(radius = entitySize / 2f) {
                        density = 10f
                        friction = 0.5f
                        restitution = 0f
                        filter.categoryBits = entityType
//                filter.maskBits = TypeComponent.PLAYER or TypeComponent.BULLET or TypeComponent.ENEMY
                        filter.groupIndex = 1
                    }
                    position.set(x, y)
                    userData = (this@entity).entity
                }
            }
            with<CollisionComponent>()
            with<HealthBarComponent>()
            with<BindComponent> { entity = playerEntity }
            with<ClickableComponent> { circle.set(x, y, entitySize/2)}
        }
    }
}