package com.divelix.skitter.gameplay

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectSet
import ktx.math.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

enum class Behavior {
    WANDER, // moves randomly in own direction
    SEEK, // moves to player
    FLEE, // moves from player
    ARRIVE, // moves to player and slows down proportional to distance
    PURSUIT, // as arrive, but predicts player position
    SEPARATION, // force away from neighbors
    ALIGNMENT, // averages own velocity according to its neighbors velocity
    COHESION, // forces to neighbors average position
    OBSTACLE_AVOIDANCE
}

class BehaviorPlanner {
    var behaviors: ObjectSet<Behavior>? = null
    var maxSpeed = 0f
    var maxForce = 0f
    var critDstToObs = 2f

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
    val size = Vector2()

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

    private fun separation(agent: Body): Vector2 {
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

    private fun alignment(agent: Body): Vector2 {
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

    private fun cohesion(agent: Body): Vector2 {
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

    private fun seek(agent: Body): Vector2 {
        if (player == null) return diff.setZero()
        diff.set(player!!.position).sub(agent.position).setLength(maxSpeed)
        diff.sub(agent.linearVelocity)
        diff.nor()
        diff *= maxSpeed
        diff.limit(maxForce)
        return diff
    }

    private fun flee(agent: Body): Vector2 = -seek(agent)

    private fun arrive(agent: Body): Vector2 {
        val minDistance = 2f
        val maxDistance = 7f
        diff.set(player!!.position).sub(agent.position)
        val distance = diff.len()
        val speed = if (distance < maxDistance) maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else maxSpeed
        diff.setLength(speed).sub(agent.linearVelocity)
        return diff
    }

    private fun pursuit(agent: Body): Vector2 {
        val minDistance = 2f
        val maxDistance = 7f
        diff.set(player!!.position).sub(agent.position)
        val distance = diff.len()
        val speed = if (distance < maxDistance) maxSpeed * (distance - minDistance) / (maxDistance - minDistance) else maxSpeed
        diff.setLength(speed).sub(agent.linearVelocity)
        diff.add(player!!.linearVelocity)
        return diff
    }

    private fun avoidCircles(agent: Body): Vector2 {
        for (circle in circles) {
            val distance = agent.position.dst(circle.position) - (circle.fixtureList[0].shape as CircleShape).radius
            if (distance < critDstToObs) {
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

    private fun avoidWalls(agent: Body): Vector2 {
        for (wall in walls) {
            val edge = wall.fixtureList[0].shape as EdgeShape
            edge.getVertex1(p1)
            edge.getVertex2(p2)
            p1.set(wall.getWorldPoint(p1))
            p2.set(wall.getWorldPoint(p2))
            if (dstBetweenPointAndLine(agent.position, p1, p2) < critDstToObs) {
                diff.set(p2).sub(p1)
                val angle = if (agent.position.sub(p1).angleDeg(diff) < 0f) 90f else -90f
                diff.nor()
                diff.rotateDeg(angle)
//                println(diff)
                wallObsForce += diff
            }
        }
        wallObsForce.nor()
        wallObsForce *= maxSpeed
        wallObsForce.limit(maxForce)
        return wallObsForce
    }

    private fun avoidRects(agent: Body): Vector2 {
        for (rect in rects) {
            val rectangle = rect.fixtureList[0].shape as PolygonShape
            rectangle.getVertex(0, p1)// l-b
//            rectangle.getVertex(1, p2)// r-b
            rectangle.getVertex(2, p2)// r-t
//            rectangle.getVertex(3, p4)// l-t
            size.set(p2).sub(p1)
            val dst2rect = dstToRect(agent.position, rect.position, size)
            if (dst2rect < critDstToObs) {
                diff.set(agent.position).sub(rect.position)
                val force = Vector2()
                val signX = if (diff.x > 0f) 1f else -1f
                val signY = if (diff.y > 0f) 1f else -1f
                if (abs(diff.x) - size.x / 2f > 0f) force.x += signX
                if (abs(diff.y) - size.y / 2f > 0f) force.y += signY
                rectObsForce += force
            }
        }
        rectObsForce.nor()
        rectObsForce *= maxSpeed
        rectObsForce.limit(maxForce)
        return rectObsForce
    }

    private fun avoidObs(agent: Body): Vector2 {
        avoidCircles(agent)
        avoidWalls(agent)
        avoidRects(agent)
        return circleObsForce.add(wallObsForce).add(rectObsForce)
    }

    fun computeSteering(agent: Body): Vector2 {
        if (behaviors == null) return steeringForce
        behaviors!!.forEach {
            steeringForce += when (it) {
                Behavior.WANDER -> wander(agent)
                Behavior.SEEK -> seek(agent)
                Behavior.FLEE -> flee(agent)
                Behavior.ARRIVE -> arrive(agent)
                Behavior.PURSUIT -> pursuit(agent)
                Behavior.SEPARATION -> separation(agent)
                Behavior.ALIGNMENT -> alignment(agent)
                Behavior.COHESION -> cohesion(agent)
                Behavior.OBSTACLE_AVOIDANCE -> avoidObs(agent)
            }
        }
        steeringForce /= agent.mass
        return steeringForce
    }

    fun reset() {
        player = null
        neighbors.clear()
        circles.clear()
        walls.clear()
        rects.clear()
        separationForce.setZero()
        alignmentForce.setZero()
        cohesionForce.setZero()
        circleObsForce.setZero()
        wallObsForce.setZero()
        rectObsForce.setZero()
        steeringForce.setZero()
    }

    private fun dstBetweenPointAndLine(p0: Vector2, p1: Vector2, p2: Vector2): Float {
        a.set(p1).sub(p0)
        b.set(p2).sub(p0)
        c.set(p1).sub(p2)
        return abs(a.crs(b)) / c.len()
    }

    private fun dstToRect(p: Vector2, rectCenter: Vector2, rectSize: Vector2): Float {
        val dx = max(abs(p.x - rectCenter.x) - rectSize.x / 2f, 0f)
        val dy = max(abs(p.y - rectCenter.y) - rectSize.y / 2f, 0f)
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat() //TODO get rid of casts and sqrt
    }
}