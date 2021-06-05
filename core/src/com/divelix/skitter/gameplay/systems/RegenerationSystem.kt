package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.data.ActivePlayerData
import com.divelix.skitter.gameplay.components.HealthComponent
import com.divelix.skitter.gameplay.components.RegenerationComponent
import ktx.ashley.allOf
import ktx.ashley.get

class RegenerationSystem(private val activePlayerData: ActivePlayerData,
                         interval: Float
) : IntervalIteratingSystem(allOf(RegenerationComponent::class, HealthComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
        val regenCmp = entity[RegenerationComponent.mapper]
        val healthCmp = entity[HealthComponent.mapper]
        require(regenCmp != null && healthCmp != null) {"Entity $entity don't have necessary components for RegenerationSystem"}

        if (healthCmp.currentHealth < activePlayerData.shipHealth)
            healthCmp.currentHealth += regenCmp.amount
    }
}