package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.gameplay.components.B2dBodyComponent
import com.divelix.skitter.gameplay.components.JumperComponent
import ktx.ashley.allOf
import ktx.ashley.get

class JumperSystem: IntervalIteratingSystem(allOf(JumperComponent::class).get(), 3f) {
    val force = 5000f
    val direction = Vector2(force, 0f)

    override fun processEntity(entity: Entity) {
        val bodyCmp = entity[B2dBodyComponent.mapper]!!
        direction.rotateRad(MathUtils.random(MathUtils.PI2))
        bodyCmp.body.applyForceToCenter(direction, true)
    }
}