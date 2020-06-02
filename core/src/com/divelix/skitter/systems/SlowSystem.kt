package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.SlowComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.math.timesAssign

class SlowSystem: IteratingSystem(allOf(SlowComponent::class).get()) {
    private val reducedVelocity = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val slowRate = entity[SlowComponent.mapper]!!.slowRate
        val body = entity[B2dBodyComponent.mapper]!!.body
        reducedVelocity.set(body.linearVelocity).scl(slowRate)
        body.linearVelocity = reducedVelocity
    }
}