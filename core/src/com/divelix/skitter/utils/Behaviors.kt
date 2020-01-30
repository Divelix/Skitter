package com.divelix.skitter.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Array
import ktx.math.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Behaviors {
    val neighbors = Array<Body>()
    val separationForce = Vector2()
    val alignmentForce = Vector2()
    val cohesionForce = Vector2()
    val steeringForce = Vector2()

    val maxSpeed = 20f
    val maxForce = 10f

    // wander()
    private val circleCenter = Vector2()
    private val circleDistance = 3f
    private val circleRadius = 5f
    private val displacement = Vector2(0f, 1f).scl(circleRadius)
    private var wanderAngle = 0f
    private val wanderAngleDelta = MathUtils.PI2

    val desiredSeparation = 5f
    val diff = Vector2()

    fun reset() {
        neighbors.clear()
        separationForce.setZero()
        alignmentForce.setZero()
        cohesionForce.setZero()
        steeringForce.setZero()
    }

    fun wander(agent: Body): Vector2 {
        circleCenter.set(agent.linearVelocity).nor().scl(circleDistance)
        wanderAngle += MathUtils.random() * wanderAngleDelta - wanderAngleDelta / 2f
        displacement.rotateRad(wanderAngle)
        circleCenter.add(displacement)
        return circleCenter.nor()
    }

    fun separation(agent: Body): Vector2 {
        for (neighbor in neighbors) {
            val distance = agent.position.dst(neighbor.position)
            if (distance < desiredSeparation) {
                diff.set(agent.position).sub(neighbor.position)
                diff.nor()
                diff /= distance//.limit(1f)
                separationForce += diff
            }
        }
        separationForce.nor()
        separationForce *= maxSpeed
        separationForce.limit(maxForce)
        return separationForce
    }

    fun alignment(agent: Body): Vector2 {
        for (neighbor in neighbors) {
            alignmentForce += neighbor.linearVelocity
        }
        alignmentForce /= if (neighbors.size != 0) neighbors.size else 1 // avoid division by 0
        alignmentForce.nor()
        alignmentForce *= maxSpeed
        alignmentForce -= agent.linearVelocity
        alignmentForce.limit(maxForce)
        return alignmentForce
    }

    fun cohesion(agent: Body): Vector2 {
        if (neighbors.size == 0) return cohesionForce
        for (neighbor in neighbors)
            cohesionForce += neighbor.position
        cohesionForce /= if (neighbors.size != 0) neighbors.size else 1
        cohesionForce -= agent.position
        cohesionForce.nor()
        cohesionForce *= maxSpeed
        cohesionForce.limit(maxForce)
        return  cohesionForce
    }

    fun computeSteering(agent: Body): Vector2 {
        steeringForce += separation(agent)
        steeringForce += alignment(agent)
        steeringForce += cohesion(agent)
        return steeringForce
    }

//    // pursuit()
//    private val maxDistance = 7f
//    private val minDistance = 2f
//    private val difference = Vector2()
//
//    // align()
//    var neighborsCount = 0
//    val alignVel = Vector2()
//    val cohesionVel = Vector2()

//    //    obstacle avoidance vals
//    private val p0 = Vector2()
//    private val p1 = Vector2()
//    private val p2 = Vector2()
//    private val normal = Vector2()

//    fun seek(): Vector2 {
//        val desired = targetPos.sub(agentPos).setLength(maxSpeed)
//        desired.sub(agentVel)
//        return desired.nor()
//    }
//
//    fun flee(): Vector2 {
//        val desired = agentPos.sub(targetPos).setLength(maxSpeed)
//        desired.sub(agentVel)
//        return desired.nor()
//    }

//    fun arrive(): Vector2 {
//        difference.set(targetPos).sub(agentPos)
//        val distance = difference.len()
//        val speed = if (distance < maxDistance) maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else maxSpeed
//        val desired = difference.setLength(speed).sub(agentVel)
//        return desired.nor()
//    }
//
//    fun pursuit(): Vector2 {
//        difference.set(targetPos).sub(agentPos)
//        val distance = difference.len()
//        val speed = if (distance < maxDistance) maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else maxSpeed
//        val desired = difference.setLength(speed).sub(agentVel)
//        desired.add(targetVel)
//        return desired.nor()
//    }

//    fun avoidWall(edge: EdgeShape): Vector2 {
//        p0.set(agentPos)
//        edge.getVertex1(p1)
//        edge.getVertex2(p2)
//        normal.set(p2).sub(p1)
//        val angle = if (p0.sub(p1).angle(normal) < 0) 90f else -90f
//        normal.rotate(angle)
//        val dstToObs = dstBetweenPointAndLine(agentPos, p1, p2)
//        return if (dstToObs < 2f) normal.nor() else normal.setZero()
//    }
//
//    fun avoidCircle(circle: CircleShape): Vector2 {
//        normal.set(agentPos.sub(targetPos))
//        val dstToObs = normal.len() - circle.radius
//        return if (dstToObs < 2f) normal.nor() else normal.setZero()
//    }

    private fun dstBetweenPointAndLine(p0: Vector2, p1: Vector2, p2: Vector2): Float {
        val nom = (p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x
        val den = (p2.y - p1.y).pow(2) + (p2.x - p1.x).pow(2)
        return abs(nom) / sqrt(den)
    }
}