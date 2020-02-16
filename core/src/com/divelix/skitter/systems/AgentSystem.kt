package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Shape
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.Behaviors
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.divAssign
import ktx.math.plusAssign

class AgentSystem: IteratingSystem(allOf(AgentComponent::class).get()) {
    private val cmAgent = mapperFor<AgentComponent>()
    private val cmSteer = mapperFor<SteerComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()
    private val cmType = mapperFor<TypeComponent>()

    private val behaviors = Behaviors()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if(PlayScreen.isPaused) return
        val agentCmp = cmAgent.get(entity)
        val bodyCmp = cmBody.get(entity)
        val steerCmp = cmSteer.get(entity)

        behaviors.reset()
        for (seen in agentCmp.visibleEntities) {
            val seenBodyCmp = cmBody.get(seen) ?: return // elvis avoids NPE on bulk removal (over 200 entities)
            if (seenBodyCmp.isDead) {
                agentCmp.visibleEntities.remove(seen)
                continue
            }

            when (cmType.get(seen).type) {
                TypeComponent.PLAYER -> behaviors.player = seenBodyCmp.body
                TypeComponent.AGENT -> behaviors.neighbors.add(seenBodyCmp.body)
                TypeComponent.OBSTACLE -> {
                    val obs = seenBodyCmp.body.fixtureList[0].shape
                    when (obs.type) {
                        Shape.Type.Circle -> behaviors.circles.add(seenBodyCmp.body)
                        Shape.Type.Edge -> behaviors.walls.add(seenBodyCmp.body)
                        Shape.Type.Polygon -> behaviors.rects.add(seenBodyCmp.body)
                        else -> println("This shape was not implemented")
                    }
                }
            }
        }
//        if (steering.len2() < 0.1f) steering += behaviors.wander()
        val steering = behaviors.computeSteering(bodyCmp.body)
        steering.setLength(30f) // TODO hardcoded length
        steerCmp.steeringForce.set(steering)

//        // TODO crutch for teleport on edges (remove later)
//        val bodyX = bodyCmp.body.position.x
//        val bodyY = bodyCmp.body.position.y
//        val bodyA = bodyCmp.body.angle
//        if (bodyX < -8f) bodyCmp.body.setTransform(50f, bodyY, bodyA)
//        if (bodyY < -8f) bodyCmp.body.setTransform(bodyX, 50f, bodyA)
//        if (bodyX > 50f) bodyCmp.body.setTransform(-8f, bodyY, bodyA)
//        if (bodyY > 50f) bodyCmp.body.setTransform(bodyX, -8f, bodyA)
    }
}