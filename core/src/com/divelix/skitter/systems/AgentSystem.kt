package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.Shape
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.Behaviors
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.math.*

class AgentSystem: IteratingSystem(allOf(AgentComponent::class).get()) {
    private val cmAgent = mapperFor<AgentComponent>()
    private val cmSteer = mapperFor<SteerComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()
    private val cmType = mapperFor<TypeComponent>()

    private val behaviors = Behaviors()
    private val steering = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if(PlayScreen.isPaused) return
        val agentCmp = cmAgent.get(entity)
        val bodyCmp = cmBody.get(entity)
        val steerCmp = cmSteer.get(entity)
        behaviors.maxSpeed = steerCmp.maxSpeed
        behaviors.agentPos.set(bodyCmp.body.position)
//        println(bodyCmp.body.getWorldPoint(steerCmp.steeringPoint))
        behaviors.agentVel.set(bodyCmp.body.linearVelocity)

        steering.setZero()
        if (agentCmp.visibleEntities.size > 0) {
            for (seen in agentCmp.visibleEntities) {
                val seenBodyCmp = cmBody.get(seen)
                if (seenBodyCmp.isDead) {
                    agentCmp.visibleEntities.remove(seen)
                    continue
                }
                behaviors.targetPos.set(seenBodyCmp.body.position)
                behaviors.targetVel.set(seenBodyCmp.body.linearVelocity)

                when (cmType.get(seen).type) {
                    TypeComponent.PLAYER -> steering += behaviors.pursuit()
//                    TypeComponent.ENEMY -> {
//                        behaviors.neighborsCount++
//                        behaviors.acumAlign()
////                        behaviors.acumCohesion()
//                    }
                    TypeComponent.OBSTACLE -> {
                        val obs = seenBodyCmp.body.fixtureList[0].shape
                        when (obs.type) {
                            Shape.Type.Edge -> steering += behaviors.avoidWall(obs as EdgeShape)
                            Shape.Type.Circle -> steering += behaviors.avoidCircle(obs as CircleShape)
                            else -> println("This shape was not implemented")
                        }
                    }
                }
            }
            steering += behaviors.align()
            steering += behaviors.cohesion()
            behaviors.alignVel.setZero()
            behaviors.cohesionVel.setZero()
        }
//        if (steering.len2() < 0.1f) steering += behaviors.wander()
        steering.scl(1f / bodyCmp.body.mass).setLength(30f) // TODO hardcoded length
        steerCmp.steeringForce.set(steering)

        // TODO delete this kostyl that was made for tests
        val bodyX = bodyCmp.body.position.x
        val bodyY = bodyCmp.body.position.y
        val bodyA = bodyCmp.body.angle
        if (bodyX < -8f) bodyCmp.body.setTransform(50f, bodyY, bodyA)
        if (bodyY < -8f) bodyCmp.body.setTransform(bodyX, 50f, bodyA)
        if (bodyX > 50f) bodyCmp.body.setTransform(-8f, bodyY, bodyA)
        if (bodyY > 50f) bodyCmp.body.setTransform(bodyX, -8f, bodyA)
    }
}