package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Shape
import com.divelix.skitter.gameplay.components.B2dBodyComponent
import com.divelix.skitter.gameplay.components.SteerComponent
import com.divelix.skitter.gameplay.components.TypeComponent
import com.divelix.skitter.gameplay.components.VisionComponent
import com.divelix.skitter.gameplay.BehaviorPlanner
import ktx.ashley.allOf
import ktx.ashley.get

class BehaviorSystem: IteratingSystem(allOf(VisionComponent::class, SteerComponent::class).get()) {
    private val behaviorPlanner = BehaviorPlanner()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val visionCmp = entity[VisionComponent.mapper]
        val bodyCmp = entity[B2dBodyComponent.mapper]
        val steerCmp = entity[SteerComponent.mapper]
        require(visionCmp != null && bodyCmp != null && steerCmp != null) { "Entity $entity don't have necessary components for BehaviorSystem" }

        behaviorPlanner.run {
            reset()
            behaviors = steerCmp.behaviors
            maxSpeed = steerCmp.maxSpeed
            maxForce = steerCmp.maxForce
        }
//        behaviorPlanner.behaviors = steerCmp.behaviors
//        println(steerCmp.behaviors)
        for (seen in visionCmp.visibleEntities) {
            val seenBodyCmp = seen[B2dBodyComponent.mapper] ?: return // elvis avoids NPE on bulk removal (over 200 entities)
            //TODO fix later
//            if (seenBodyCmp.isDead) {
//                visionCmp.visibleEntities.remove(seen)
//                continue
//            }

            when (seen[TypeComponent.mapper]!!.type) {
                TypeComponent.PLAYER -> behaviorPlanner.player = seenBodyCmp.body
                TypeComponent.ENEMY -> behaviorPlanner.neighbors.add(seenBodyCmp.body)
                TypeComponent.OBSTACLE -> {
                    val obs = seenBodyCmp.body.fixtureList[0].shape
                    when (obs.type) {
                        Shape.Type.Circle -> behaviorPlanner.circles.add(seenBodyCmp.body)
                        Shape.Type.Edge -> behaviorPlanner.walls.add(seenBodyCmp.body)
                        Shape.Type.Polygon -> behaviorPlanner.rects.add(seenBodyCmp.body)
                        else -> println("This shape was not implemented")
                    }
                }
            }
        }
        val steering = behaviorPlanner.computeSteering(bodyCmp.body)
        steering.setLength(steerCmp.finalForce)
        steerCmp.steeringForce.set(steering)
        bodyCmp.body.applyForce(steerCmp.steeringForce, bodyCmp.body.getWorldPoint(steerCmp.steeringPoint), true)
    }
}