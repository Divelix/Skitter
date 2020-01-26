package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.Shape
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen
import ktx.math.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

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
//    obstacle avoid vals
    private val p0 = Vector2()
    private val p1 = Vector2()
    private val p2 = Vector2()
    private val normal = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if(PlayScreen.isPaused) return
        val agentCmp = cmAgent.get(entity)
        val bodyCmp = cmBody.get(entity)
        val steerCmp = cmSteer.get(entity)

        fun wander(): Vector2 {
            circleCenter.set(bodyCmp.body.linearVelocity).nor().scl(circleDistance)
            wanderAngle += MathUtils.random() * wanderAngleDelta - wanderAngleDelta / 2f
            displacement.rotateRad(wanderAngle)
            circleCenter.add(displacement)
            return circleCenter.nor()
        }

        fun seek(): Vector2 {
            val desired = targetPos.sub(agentPos).setLength(steerCmp.maxSpeed)
            desired.sub(bodyCmp.body.linearVelocity)
            return desired.nor()
        }

        fun flee(): Vector2 {
            val desired = agentPos.sub(targetPos).setLength(steerCmp.maxSpeed)
            desired.sub(bodyCmp.body.linearVelocity)
            return desired.nor()
        }

        fun avoidWall(edge: EdgeShape): Vector2 {
            p0.set(agentPos)
            edge.getVertex1(p1)
            edge.getVertex2(p2)
            normal.set(p2).sub(p1)
            val angle = if (p0.sub(p1).angle(normal) < 0) 90f else -90f
            normal.rotate(angle)
            val dstToObs = dstBetweenPointAndLine(agentPos, p1, p2)
            return if (dstToObs < 2f) normal.nor() else normal.setZero()
        }

        fun avoidCircle(circle: CircleShape): Vector2 {
            normal.set(agentPos.sub(targetPos))
            val dstToObs = normal.len() - circle.radius
            return if (dstToObs < 2f) normal.nor() else normal.setZero()
        }

        steering.setZero()
        if (agentCmp.visibleEntities.size == 0) {
            steering += wander()
        } else {
            for (seen in agentCmp.visibleEntities) {
                val seenBodyCmp = cmBody.get(seen)
                if (seenBodyCmp.isDead) {
                    agentCmp.visibleEntities.remove(seen)
                    continue
                }
                targetPos.set(seenBodyCmp.body.position)
                agentPos.set(bodyCmp.body.position)

                when (cmType.get(seen).type) {
                    TypeComponent.PLAYER -> steering += flee()
                    TypeComponent.ENEMY -> steering += seek()
                    TypeComponent.OBSTACLE -> {
                        val obs = seenBodyCmp.body.fixtureList[0].shape
                        when (obs.type) {
                            Shape.Type.Edge -> steering += avoidWall(obs as EdgeShape)
                            Shape.Type.Circle -> steering += avoidCircle(obs as CircleShape)
                            else -> println("This shape was not implemented")
                        }
                    }
                }
            }
        }
        steering.scl(1f / bodyCmp.body.mass).setLength(50f) // TODO hadcoded 50f
        steerCmp.steeringForce.set(steering)
    }

    private fun dstBetweenPointAndLine(p0: Vector2, p1: Vector2, p2: Vector2): Float {
        val nom = (p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x
        val den = (p2.y - p1.y).pow(2) + (p2.x - p1.x).pow(2)
        return abs(nom) / sqrt(den)
    }
}