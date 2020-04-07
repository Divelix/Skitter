package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.utils.Behaviors
import ktx.collections.*

class SteerComponent: Component, Pool.Poolable {
    val behaviors = gdxSetOf<Behaviors>()
    val steeringPoint = Vector2()
    val steeringForce = Vector2()
    var maxSpeed = 0f
    var maxForce = 0f
    var finalForce = 0f

    override fun reset() {
        behaviors.clear()
        steeringPoint.setZero()
        steeringForce.setZero()
        maxSpeed = 0f
        maxForce = 0f
        finalForce = 0f
    }
}