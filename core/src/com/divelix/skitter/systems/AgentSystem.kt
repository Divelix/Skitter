package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.Shape
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen

class AgentSystem: IteratingSystem(Family.all(AgentComponent::class.java).get()) {
    private val cmAgent = ComponentMapper.getFor(AgentComponent::class.java)
    private val cmSteer = ComponentMapper.getFor(SteerComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)
    private val cmType = ComponentMapper.getFor(TypeComponent::class.java)
    private val agentPos = Vector2()
    private val targetPos = Vector2()
    private val steering = Vector2()

//    values for wander()
    private val circleCenter = Vector2()
    private val circleDistance = 3f
    private val circleRadius = 5f
    private val displacement = Vector2(0f, 1f).scl(circleRadius)
    private var wanderAngle = 0f
    private val wanderAngleDelta = MathUtils.PI2

    private val p0 = Vector2()
    private val p1 = Vector2()
    private val p2 = Vector2()
    private val normal = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if(PlayScreen.isPaused) return
        val agentCmp = cmAgent.get(entity)
        val bodyCmp = cmBody.get(entity)
        val steerCmp = cmSteer.get(entity)

        fun wander() {
            circleCenter.set(bodyCmp.body.linearVelocity).nor().scl(circleDistance)
            wanderAngle += MathUtils.random() * wanderAngleDelta - wanderAngleDelta / 2f
            displacement.rotateRad(wanderAngle)
            val wanderForce = circleCenter.add(displacement)
            steering.add(wanderForce)
        }

        fun seek() {
            val desired = targetPos.sub(agentPos).setLength(steerCmp.maxSpeed)
            steering.add(desired.sub(bodyCmp.body.linearVelocity).limit(steerCmp.maxForce))
        }

        fun flee() {
            val desired = agentPos.sub(targetPos).setLength(steerCmp.maxSpeed)
            steering.add(desired.sub(bodyCmp.body.linearVelocity).limit(steerCmp.maxForce))
        }

        steering.setZero()
        if (agentCmp.visibleEntities.size == 0) {
            wander()
        } else {
            for (seen in agentCmp.visibleEntities) {
                val seenBodyCmp = cmBody.get(seen)
                if (seenBodyCmp.isDead) {
                    agentCmp.visibleEntities.removeValue(seen, true)
                    continue
                }
                targetPos.set(seenBodyCmp.body.position)
                agentPos.set(bodyCmp.body.position)

                when (cmType.get(seen).type) {
                    TypeComponent.PLAYER -> flee()
                    TypeComponent.ENEMY -> seek()
                    TypeComponent.OBSTACLE -> { // TODO complete shit, rework
                        val obs = seenBodyCmp.body.fixtureList[0].shape
                        when (obs.type) {
                            Shape.Type.Edge -> { obs as EdgeShape
                                p0.set(agentPos)
                                obs.getVertex1(p1)
                                obs.getVertex2(p2)

                                normal.set(p2).sub(p1)
                                val angle = if (p0.sub(p1).angle(normal) < 0) 90f else -90f
                                normal.rotate(angle)
                            }
                            else -> println("Only edge shape was implemented")
                        }
                        val desired = normal.setLength(steerCmp.maxSpeed)
                        steering.add(desired.limit(steerCmp.maxForce / 2f))
                    }
                }
            }
        }
        steering.scl(1f / bodyCmp.body.mass).limit(steerCmp.maxForce)
        steerCmp.steering.set(steering)
    }
}