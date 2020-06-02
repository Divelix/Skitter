package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.components.HealthComponent
import ktx.ashley.allOf
import ktx.ashley.get

class DecaySystem(interval: Float): IntervalIteratingSystem(allOf(DecayComponent::class, HealthComponent::class).get(), interval) {
    override fun processEntity(entity: Entity) {
        val decayCmp = entity[DecayComponent.mapper]!!
        val healthCmp = entity[HealthComponent.mapper]!!

        if (healthCmp.health > decayCmp.damage)
            healthCmp.health -= decayCmp.damage
        else
            healthCmp.health = 0f
    }
}