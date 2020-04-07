package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.components.HealthComponent
import ktx.ashley.allOf

class DecaySystem(interval: Float): IntervalIteratingSystem(allOf(DecayComponent::class, HealthComponent::class).get(), interval) {
    override fun processEntity(entity: Entity?) {
        val decayCmp = GameEngine.cmDecay.get(entity)
        val healthCmp = GameEngine.cmHealth.get(entity)

        if (healthCmp.health > decayCmp.damage)
            healthCmp.health -= decayCmp.damage
        else
            healthCmp.health = 0f
    }
}