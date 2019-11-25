package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.DecayComponent
import com.divelix.skitter.components.HealthComponent

class DecaySystem(interval: Float): IntervalIteratingSystem(Family.all(DecayComponent::class.java, HealthComponent::class.java).get(), interval) {
    val cmHealth = ComponentMapper.getFor(HealthComponent::class.java)

    override fun processEntity(entity: Entity?) {
        val healthCmp = cmHealth.get(entity)
        if (healthCmp.health > 10f)
            healthCmp.health -= 10f
        else
            healthCmp.health = 0f
    }
}