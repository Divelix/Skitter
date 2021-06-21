package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.gameplay.components.DecayComponent
import com.divelix.skitter.gameplay.components.HealthComponent
import ktx.ashley.allOf
import ktx.ashley.get

class DecaySystem(interval: Float): IntervalIteratingSystem(allOf(DecayComponent::class, HealthComponent::class).get(), interval) {
    override fun processEntity(entity: Entity) {
        val decayCmp = entity[DecayComponent.mapper]!!
        val healthCmp = entity[HealthComponent.mapper]!!

        if (healthCmp.currentHealth > decayCmp.damage)
            healthCmp.currentHealth -= decayCmp.damage
        else
            healthCmp.currentHealth = 0f
    }
}