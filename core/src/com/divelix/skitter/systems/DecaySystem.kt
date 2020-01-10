package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.components.HealthComponent
import com.divelix.skitter.screens.PlayScreen

class DecaySystem(interval: Float): IntervalIteratingSystem(Family.all(DecayComponent::class.java, HealthComponent::class.java).get(), interval) {
    private val cmDecay = ComponentMapper.getFor(DecayComponent::class.java)
    private val cmHealth = ComponentMapper.getFor(HealthComponent::class.java)

    override fun processEntity(entity: Entity?) {
        if (PlayScreen.isPaused) return
        val decayCmp = cmDecay.get(entity)
        val healthCmp = cmHealth.get(entity)

        if (healthCmp.health > decayCmp.damage)
            healthCmp.health -= decayCmp.damage
        else
            healthCmp.health = 0f
    }
}