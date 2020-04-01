package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.components.HealthComponent
import com.divelix.skitter.screens.PlayScreen
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class DecaySystem(interval: Float): IntervalIteratingSystem(allOf(DecayComponent::class, HealthComponent::class).get(), interval) {
    private val cmDecay = mapperFor<DecayComponent>()
    private val cmHealth = mapperFor<HealthComponent>()

    override fun processEntity(entity: Entity?) {
        val decayCmp = cmDecay.get(entity)
        val healthCmp = cmHealth.get(entity)

        if (healthCmp.health > decayCmp.damage)
            healthCmp.health -= decayCmp.damage
        else
            healthCmp.health = 0f
    }
}