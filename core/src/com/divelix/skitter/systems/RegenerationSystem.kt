package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.HealthComponent
import com.divelix.skitter.components.RegenerationComponent
import com.divelix.skitter.screens.PlayScreen
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class RegenerationSystem(interval: Float): IntervalIteratingSystem(allOf(RegenerationComponent::class, HealthComponent::class).get(), interval) {
    private val cmRegen = mapperFor<RegenerationComponent>()
    private val cmHealth = mapperFor<HealthComponent>()

    override fun processEntity(entity: Entity?) {
        if (PlayScreen.isPaused) return
        val regenCmp = cmRegen.get(entity)
        val healthCmp = cmHealth.get(entity)

        if (healthCmp.health < Data.playerData.ship.health)
            healthCmp.health += regenCmp.amount
    }
}