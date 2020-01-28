package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class LoverSystem: IteratingSystem(allOf(LoverComponent::class).get()) {
    private val cmTrans = mapperFor<TransformComponent>()
    private val cmBind = mapperFor<BindComponent>()
    private val cmSteer = mapperFor<SteerComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()
    private val loverPos = Vector2()
    private val targetPos = Vector2()
    private val steering = Vector2()
    private val behavior = Behaviors.PURSUIT

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transCmp = cmTrans.get(entity)
        val bindCmp = cmBind.get(entity)
        val steerCmp = cmSteer.get(entity)
        val bodyCmp = cmBody.get(entity)
        val targetBodyCmp = cmBody.get(bindCmp.entity)
        val targetTransCmp = cmTrans.get(bindCmp.entity)

        loverPos.set(transCmp.position.x, transCmp.position.y)
        targetPos.set(targetTransCmp.position.x, targetTransCmp.position.y)

        when (behavior) {
            Behaviors.SEEK -> {
                // steering = desired - velocity
                val desired = targetPos.sub(loverPos).setLength(steerCmp.maxSpeed)
                steering.set(desired.sub(bodyCmp.body.linearVelocity).limit(steerCmp.maxForce))
            }
            Behaviors.FLEE -> {
                val desired = loverPos.sub(targetPos).setLength(steerCmp.maxSpeed)
                steering.set(desired.sub(bodyCmp.body.linearVelocity).limit(steerCmp.maxForce))
            }
            Behaviors.ARRIVE -> {
                val maxDistance = 10f
                val minDistance = 2f // lover radius + player radius
                val distance = targetPos.dst(loverPos)
                val maxSpeed = if (distance < maxDistance) steerCmp.maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else steerCmp.maxSpeed
                val desired = targetPos.sub(loverPos).setLength(maxSpeed)
                steering.set(desired.sub(bodyCmp.body.linearVelocity).limit(steerCmp.maxForce))
            }
            Behaviors.PURSUIT -> {
                val maxDistance = 10f
                val minDistance = 2f // lover radius + player radius
                val targetPrediction = targetBodyCmp.body.linearVelocity
                val distance = targetPos.dst(loverPos)
                val maxSpeed = if (distance < maxDistance) steerCmp.maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else steerCmp.maxSpeed
                val desired = targetPos.sub(loverPos).setLength(maxSpeed)
                steering.set(desired.sub(bodyCmp.body.linearVelocity).add(targetPrediction).limit(steerCmp.maxForce))
            }
        }
        steering.scl(1f / bodyCmp.body.mass)
        steerCmp.steeringForce.set(steering)
    }

    enum class Behaviors {
        SEEK, // move to target
        FLEE, // move from target
        ARRIVE, // slow down on approach
        PURSUIT // move to predicted position of target
    }
}