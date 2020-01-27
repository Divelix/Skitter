package com.divelix.skitter.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Behaviors {
    var maxSpeed = 0f
    val agentPos = Vector2()
    val targetPos = Vector2()
    val agentVel = Vector2()
    val targetVel = Vector2()
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

    private var neighborsCount = 0
    val totalVel = Vector2()

    fun wander(): Vector2 {
        circleCenter.set(agentVel).nor().scl(circleDistance)
        wanderAngle += MathUtils.random() * wanderAngleDelta - wanderAngleDelta / 2f
        displacement.rotateRad(wanderAngle)
        circleCenter.add(displacement)
        return circleCenter.nor()
    }

    fun seek(): Vector2 {
        val desired = targetPos.sub(agentPos).setLength(maxSpeed)
        desired.sub(agentVel)
        return desired.nor()
    }

    fun flee(): Vector2 {
        val desired = agentPos.sub(targetPos).setLength(maxSpeed)
        desired.sub(agentVel)
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

    fun align(): Vector2 {
        return if (neighborsCount > 0) totalVel.scl(1f / neighborsCount) else totalVel.setZero()
    }

    fun acumAlign() {
        neighborsCount++
        totalVel.add(targetVel)
    }

    private fun dstBetweenPointAndLine(p0: Vector2, p1: Vector2, p2: Vector2): Float {
        val nom = (p2.y - p1.y) * p0.x - (p2.x - p1.x) * p0.y + p2.x * p1.y - p2.y * p1.x
        val den = (p2.y - p1.y).pow(2) + (p2.x - p1.x).pow(2)
        return abs(nom) / sqrt(den)
    }
}