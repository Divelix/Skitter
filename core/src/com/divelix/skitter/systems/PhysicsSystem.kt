package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.PlayerData
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.TransformComponent
import com.divelix.skitter.screens.PlayScreen

class PhysicsSystem(private val world: World, private val blackList: ArrayList<Body>):
        IteratingSystem(Family.all(B2dBodyComponent::class.java, TransformComponent::class.java).get()) {

    private val bodiesQueue: Array<Entity> = Array()
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)

    private var accumulator = 0f
    private var reloadTimer = 0f
    private val reloadTime = Data.playerData.gun.reloadTime
    private val capacity = Data.playerData.gun.capacity

    override fun update(deltaTime: Float) {
        if(PlayScreen.isPaused) return
        super.update(deltaTime)
        Data.renderTime += deltaTime
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        if (accumulator >= Constants.B2D_STEP_TIME) {
            val stepTime = Constants.B2D_STEP_TIME / PlayScreen.slowRate
            Data.physicsTime += stepTime
            // reload ammo
            reloadTimer += stepTime
            if (reloadTimer >= reloadTime) {
                if (Data.dynamicData.ammo < capacity) Data.dynamicData.ammo++
                reloadTimer = 0f
            }
            world.step(stepTime, 6, 2)
            accumulator -= stepTime

            //Loop through all Entities and update its components
            for (entity in bodiesQueue) {
                // get components
                val tfmCmp = cmTrans.get(entity)
                val bodyCmp = cmBody.get(entity)
                // get position from body
                val position = bodyCmp.body.position
                // update our transform to match body position
                tfmCmp.position.x = position.x
                tfmCmp.position.y = position.y
                tfmCmp.rotation = bodyCmp.body.angle * MathUtils.radiansToDegrees
                if (bodyCmp.isDead) {
                    if (!blackList.contains(bodyCmp.body))
                        blackList.add(bodyCmp.body)
                }
            }
        }
        bodiesQueue.clear()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        // add Items to queue
        bodiesQueue.add(entity)
    }
}