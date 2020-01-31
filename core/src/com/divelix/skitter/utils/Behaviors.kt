package com.divelix.skitter.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.utils.Array
import ktx.math.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Behaviors {
    val neighbors = Array<Body>()
    val circles = Array<Body>()
    val walls = Array<Body>()

    private val wanderForce = Vector2()
    private val separationForce = Vector2()
    private val alignmentForce = Vector2()
    private val cohesionForce = Vector2()
    private val circleObsForce = Vector2()
    private val wallObsForce = Vector2()
    private val steeringForce = Vector2()

    val maxSpeed = 20f
    val maxForce = 20f

    // wander()
    private val circleCenter = Vector2()
    private val circleDistance = 3f
    private val circleRadius = 5f
    private val displacement = Vector2(0f, 1f).scl(circleRadius)
    private var wanderAngle = 0f
    private val wanderAngleDelta = MathUtils.PI2

    val diff = Vector2()
    val p1 = Vector2()
    val p2 = Vector2()
    val a = Vector2()
    val b = Vector2()
    val c = Vector2()

    fun reset() {
        neighbors.clear()
        circles.clear()
        walls.clear()
        separationForce.setZero()
        alignmentForce.setZero()
        cohesionForce.setZero()
        circleObsForce.setZero()
        wallObsForce.setZero()
        steeringForce.setZero()
    }

    fun wander(agent: Body): Vector2 {
        circleCenter.set(agent.linearVelocity).nor().scl(circleDistance)
        wanderAngle += MathUtils.random() * wanderAngleDelta - wanderAngleDelta / 2f
        displacement.rotateRad(wanderAngle)
        circleCenter += displacement
        wanderForce.set(circleCenter)
        wanderForce.nor()
        wanderForce *= maxSpeed
        wanderForce.limit(maxForce)
        return wanderForce
    }

    fun separation(agent: Body): Vector2 {
        for (neighbor in neighbors) {
            val distance = agent.position.dst(neighbor.position)
            if (distance < 5f) {
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

    fun avoidCircles(agent: Body): Vector2 {
        for (circle in circles) {
            val distance = agent.position.dst(circle.position) - (circle.fixtureList[0].shape as CircleShape).radius
            if (distance < 5f) {
                diff.set(agent.position).sub(circle.position)
                diff.nor()
                diff /= distance
                circleObsForce += diff
            }
        }
        circleObsForce.nor()
        circleObsForce *= maxSpeed
        circleObsForce.limit(maxForce)
        return circleObsForce
    }


    fun avoidWalls(agent: Body): Vector2 {
        for (wall in walls) {
            val edge = wall.fixtureList[0].shape as EdgeShape
            edge.getVertex1(p1)
            edge.getVertex2(p2)
            if (dstBetweenPointAndLine(agent.position, p1, p2) < 5f) {
                diff.set(p2).sub(p1)
                val angle = if (agent.position.sub(p1).angle(diff) < 0f) 90f else -90f
                diff.nor()
                diff.rotate(angle)
//                println(diff)
                wallObsForce += diff
            }
        }
        wallObsForce.nor()
        wallObsForce *= maxSpeed
        wallObsForce.limit(maxForce)
        return wallObsForce
    }

    fun computeSteering(agent: Body): Vector2 {
        steeringForce += separation(agent)
        steeringForce += alignment(agent)
        steeringForce += cohesion(agent)
//        steeringForce += wander(agent)
        steeringForce += avoidCircles(agent)
        steeringForce += avoidWalls(agent)

        steeringForce /= agent.mass
        return steeringForce
    }

//    // pursuit()
//    private val maxDistance = 7f
//    private val minDistance = 2f
//    private val difference = Vector2()

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

    private fun dstBetweenPointAndLine(p0: Vector2, p1: Vector2, p2: Vector2): Float {
//        val nom = (p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x
//        val den = (p2.y - p1.y).pow(2) + (p2.x - p1.x).pow(2)
//        return abs(nom) / sqrt(den)
        a.set(p1).sub(p0)
        b.set(p2).sub(p0)
        c.set(p1).sub(p2)
        return abs(a.crs(b)) / c.len()
    }
}