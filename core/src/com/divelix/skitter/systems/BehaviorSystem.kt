package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Shape
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.BehaviorPlanner
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class BehaviorSystem: IteratingSystem(allOf(VisionComponent::class, SteerComponent::class).get()) {
    private val cmVision = mapperFor<VisionComponent>()
    private val cmSteer = mapperFor<SteerComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()
    private val cmType = mapperFor<TypeComponent>()

    private val behaviorPlanner = BehaviorPlanner()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if(PlayScreen.isPaused) return
        val visionCmp = cmVision.get(entity)
        val bodyCmp = cmBody.get(entity)
        val steerCmp = cmSteer.get(entity)

        behaviorPlanner.run {
            reset()
            behaviors = steerCmp.behaviors
            maxSpeed = steerCmp.maxSpeed
            maxForce = steerCmp.maxForce
        }
        behaviorPlanner.behaviors = steerCmp.behaviors
//        println(steerCmp.behaviors)
        for (seen in visionCmp.visibleEntities) {
            val seenBodyCmp = cmBody.get(seen) ?: return // elvis avoids NPE on bulk removal (over 200 entities)
            if (seenBodyCmp.isDead) {
                visionCmp.visibleEntities.remove(seen)
                continue
            }

            when (cmType.get(seen).type) {
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