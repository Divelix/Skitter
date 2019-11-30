package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.HealthComponent
import com.divelix.skitter.components.RegenerationComponent

class RegenerationSystem(interval: Float): IntervalIteratingSystem(Family.all(RegenerationComponent::class.java, HealthComponent::class.java).get(), interval) {
    private val cmRegen = ComponentMapper.getFor(RegenerationComponent::class.java)
    private val cmHealth = ComponentMapper.getFor(HealthComponent::class.java)

    override fun processEntity(entity: Entity?) {
        val regenCmp = cmRegen.get(entity)
        val healthCmp = cmHealth.get(entity)

        if (healthCmp.health < Data.playerData.ship.health)
            healthCmp.health += regenCmp.amount
    }
}