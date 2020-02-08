package com.divelix.skitter.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Array
import ktx.math.*
import kotlin.math.abs

class Behaviors {
    val neighbors = Array<Body>()
    val circles = Array<Body>()
    val walls = Array<Body>()
    val rects = Array<Body>()
    var player: Body? = null

    private val wanderForce = Vector2()
    private val separationForce = Vector2()
    private val alignmentForce = Vector2()
    private val cohesionForce = Vector2()
    private val circleObsForce = Vector2()
    private val wallObsForce = Vector2()
    private val rectObsForce = Vector2()
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
    val p3 = Vector2()
    val p4 = Vector2()
    val a = Vector2()
    val b = Vector2()
    val c = Vector2()

    fun reset() {
        neighbors.clear()
        circles.clear()
        walls.clear()
        rects.clear()
        player = null
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
            p1.set(wall.getWorldPoint(p1))
            p2.set(wall.getWorldPoint(p2))
            if (dstBetweenPointAndLine(agent.position, p1, p2) < 3f) {
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

//    fun avoidRects(agent: Body): Vector2 {
//        for (rect in rects) {
//            val rectangle = rect.fixtureList[0].shape as PolygonShape
//            rectangle.getVertex(0, p1)
//            rectangle.getVertex(1, p2)
//            rectangle.getVertex(2, p3)
//            rectangle.getVertex(3, p4)
//            p1.set(rect.getWorldPoint(p1))
//            p2.set(rect.getWorldPoint(p2))
//            p3.set(rect.getWorldPoint(p3))
//            p4.set(rect.getWorldPoint(p4))
//        }
//        return rectObsForce
//    }

    fun seek(agent: Body): Vector2 {
        diff.set(player!!.position).sub(agent.position).setLength(maxSpeed)
        diff.sub(agent.linearVelocity)
        diff.nor()
        diff *= maxSpeed
        diff.limit(maxForce)
        return diff
    }

    fun flee(agent: Body): Vector2 = -seek(agent)

    fun arrive(agent: Body): Vector2 {
        val minDistance = 2f
        val maxDistance = 7f
        diff.set(player!!.position).sub(agent.position)
        val distance = diff.len()
        val speed = if (distance < maxDistance) maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else maxSpeed
        diff.setLength(speed).sub(agent.linearVelocity)
        return diff
    }

    fun pursuit(agent: Body): Vector2 {
        val minDistance = 2f
        val maxDistance = 7f
        diff.set(player!!.position).sub(agent.position)
        val distance = diff.len()
        val speed = if (distance < maxDistance) maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else maxSpeed
        diff.setLength(speed).sub(agent.linearVelocity)
        diff.add(player!!.linearVelocity)
        return diff
    }

    fun computeSteering(agent: Body): Vector2 {
        steeringForce += separation(agent)
        steeringForce += alignment(agent)
        steeringForce += cohesion(agent)
        steeringForce += avoidCircles(agent)
        steeringForce += avoidWalls(agent)
//        steeringForce += avoidRects(agent)
        if (player != null) steeringForce += flee(agent)
//        if (agent.linearVelocity.len2() < 400f) steeringForce += wander(agent)

        steeringForce /= agent.mass
        return steeringForce
    }

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