package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.TransformComponent
import com.divelix.skitter.screens.PlayScreen
import ktx.ashley.allOf
import kotlin.math.min

class PhysicsSystem(private val world: World):
        IteratingSystem(allOf(B2dBodyComponent::class, TransformComponent::class).get()) {

    private val entities: Array<Entity> = Array()
    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        reloadAmmo(deltaTime)
        accumulator += min(deltaTime, 0.25f)
        if (accumulator >= Constants.B2D_STEP_TIME) {
            val stepTime = Constants.B2D_STEP_TIME / GameEngine.slowRate
            Data.physicsTime += stepTime
            world.step(stepTime, 6, 2)
            accumulator -= stepTime

            //Loop through all Entities and update its components
            for (entity in entities) {
                // get components
                val tfmCmp = GameEngine.cmTransform.get(entity)
                val bodyCmp = GameEngine.cmBody.get(entity)
                // get position from body
                val position = bodyCmp.body.position
                // update our transform to match body position
                tfmCmp.position.x = position.x
                tfmCmp.position.y = position.y
                tfmCmp.rotation = bodyCmp.body.angle * MathUtils.radiansToDegrees
            }
        }
        entities.clear()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        // add Items to queue
        entities.add(entity)
    }

    fun reloadAmmo(delta: Float) {
        if (PlayScreen.ammo < Data.playerData.gun.capacity) {
            Data.reloadTimer += delta
            if (Data.reloadTimer >= Data.playerData.gun.reloadTime) {
                PlayScreen.ammo++
                Data.reloadTimer = 0f
            }
        } else {
            Data.reloadTimer = Data.playerData.gun.reloadTime
        }
    }
}