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
import ktx.box2d.body
import ktx.box2d.mouseJointWith

class EntityBuilder(private val engine: PooledEngine, private val world: World, private val assets: Assets) {

    fun createPlayer(): Entity {
        val entity = engine.createEntity()

        val typeCmp = engine.createComponent(TypeComponent::class.java)
        val playerCmp = engine.createComponent(PlayerComponent::class.java)
        val transformCmp = engine.createComponent(TransformComponent::class.java)
        val textureCmp = engine.createComponent(TextureComponent::class.java)
        val b2dCmp = engine.createComponent(B2dBodyComponent::class.java)
        val mouseCmp = engine.createComponent(MouseComponent::class.java)
        val collisionCmp = engine.createComponent(CollisionComponent::class.java)

        val type = TypeComponent.PLAYER
        typeCmp.type = type
        transformCmp.position.set(0f, 0f, 1f)
        transformCmp.size.set(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE)
        textureCmp.region = TextureRegion(assets.manager.get<Texture>(Constants.PLAYER_DEFAULT))
        val cursorBody = world.body(type = BodyDef.BodyType.StaticBody) {
            position.set(transformCmp.position.x, transformCmp.position.y)
        }
        val playerBody = world.body(type = BodyDef.BodyType.DynamicBody) {
            circle(radius = Constants.PLAYER_SIZE / 2f) {
                density = 10f
                friction = 0.5f
                restitution = 0f
                filter.categoryBits = type
                filter.maskBits = TypeComponent.ENEMY
//                filter.groupIndex = -1
                isSensor = true
            }
            fixedRotation = true
            position.set(transformCmp.position.x, transformCmp.position.y)
            userData = entity
        }
        b2dCmp.body = playerBody
        mouseCmp.mouseJoint = cursorBody.mouseJointWith(playerBody) {
            maxForce = 1000000f
            target.set(bodyB.position)
            collideConnected = false
            dampingRatio = 1f
//            frequencyHz = 100f
        }

        entity.add(typeCmp)
        entity.add(playerCmp)
        entity.add(transformCmp)
        entity.add(textureCmp)
        entity.add(b2dCmp)
        entity.add(mouseCmp)
        entity.add(collisionCmp)

        engine.addEntity(entity)
        return entity
    }

    fun createCamera(playerEntity: Entity): OrthographicCamera {
        val entity = engine.createEntity()

        val cameraCmp = engine.createComponent(CameraComponent::class.java)
        val bindCmp = engine.createComponent(BindComponent::class.java)

        cameraCmp.camera.position.set(0f, 0f, 1f)
//        cameraCmp.camera.setToOrtho(false, Constants.B2D_WIDTH, Constants.B2D_HEIGHT)
        bindCmp.entity = playerEntity

        entity.add(cameraCmp)
        entity.add(bindCmp)

        engine.addEntity(entity)
        return cameraCmp.camera
    }

    fun createBullet(playerEntity: Entity, aim: Vector2) {
        val entity = engine.createEntity()

        val typeCmp = engine.createComponent(TypeComponent::class.java)
        val bulletCmp = engine.createComponent(BulletComponent::class.java)
        val transformCmp = engine.createComponent(TransformComponent::class.java)
        val textureCmp = engine.createComponent(TextureComponent::class.java)
        val b2dCmp = engine.createComponent(B2dBodyComponent::class.java)
        val collisionCmp = engine.createComponent(CollisionComponent::class.java)
        val bindingCmp = engine.createComponent(BindComponent::class.java)

        val initPos = playerEntity.getComponent(B2dBodyComponent::class.java).body.position
        val dirVec = aim.sub(initPos)
        val angleInRad = dirVec.angleRad() - MathUtils.PI / 2f
        val type = TypeComponent.BULLET
        typeCmp.type = type
        transformCmp.position.set(initPos.x, initPos.y, 0f)
        transformCmp.size.set(bulletCmp.width, bulletCmp.height)
//        textureCmp.region = createTexture(Color.BLACK, false, (width*Main.PPM).toInt(), (height*Main.PPM).toInt())
        textureCmp.region = TextureRegion(assets.manager.get<Texture>(Constants.RIFLE))
        b2dCmp.body = world.body(type = BodyDef.BodyType.DynamicBody) {
            box(width = 0.1f, height = 0.5f) {
                density = 10f
                friction = 0.5f
                restitution = 0f
                filter.categoryBits = type
                filter.maskBits = TypeComponent.ENEMY
//                filter.groupIndex = 1
                isSensor = true // TODO Carefully
            }
            position.set(initPos)
            angle = angleInRad
            bullet = true
            userData = entity
        }
        val velocity = Vector2(0f, 1f).scl(bulletCmp.speed).rotateRad(angleInRad)
        b2dCmp.body.linearVelocity = velocity
        bindingCmp.entity = playerEntity

        entity.add(typeCmp)
        entity.add(bulletCmp)
        entity.add(transformCmp)
        entity.add(textureCmp)
        entity.add(b2dCmp)
        entity.add(collisionCmp)
        entity.add(bindingCmp)

        engine.addEntity(entity)
    }

    fun createEnemy(x: Float, y: Float, size: Float, playerEntity: Entity) {
        val entity = engine.createEntity()

        val typeCmp = engine.createComponent(TypeComponent::class.java)
        val enemyCmp = engine.createComponent(EnemyComponent::class.java)
        val transformCmp = engine.createComponent(TransformComponent::class.java)
        val textureCmp = engine.createComponent(TextureComponent::class.java)
        val b2dCmp = engine.createComponent(B2dBodyComponent::class.java)
        val collisionCmp = engine.createComponent(CollisionComponent::class.java)
        val healthBarCmp = engine.createComponent(HealthBarComponent::class.java)
        val bindingCmp = engine.createComponent(BindComponent::class.java)
        val clickCmp = engine.createComponent(ClickableComponent::class.java)

        val type = TypeComponent.ENEMY
        typeCmp.type = type
        transformCmp.position.set(x, y, 0f)
        transformCmp.size.set(size, size)
        textureCmp.region = TextureRegion(assets.manager.get<Texture>(Constants.ENEMY_DEFAULT))
        b2dCmp.body = world.body(type = BodyDef.BodyType.DynamicBody) {
            circle(radius = size / 2f) {
                density = 10f
                friction = 0.5f
                restitution = 0f
                filter.categoryBits = type
//                filter.maskBits = TypeComponent.PLAYER or TypeComponent.BULLET or TypeComponent.ENEMY
                filter.groupIndex = 1
            }
            position.set(x, y)
            userData = entity
        }
        bindingCmp.entity = playerEntity
        clickCmp.circle.set(x, y, size / 2)

        entity.add(typeCmp)
        entity.add(enemyCmp)
        entity.add(transformCmp)
        entity.add(textureCmp)
        entity.add(b2dCmp)
        entity.add(collisionCmp)
        entity.add(healthBarCmp)
        entity.add(bindingCmp)
        entity.add(clickCmp)

        engine.addEntity(entity)
    }
}