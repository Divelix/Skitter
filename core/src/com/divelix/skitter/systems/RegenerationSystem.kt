package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.HealthComponent
import com.divelix.skitter.components.RegenerationComponent
import ktx.ashley.allOf
import ktx.ashley.get

class RegenerationSystem(interval: Float): IntervalIteratingSystem(allOf(RegenerationComponent::class, HealthComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
        val regenCmp = entity[RegenerationComponent.mapper]!!
        val healthCmp = entity[HealthComponent.mapper]!!

        if (healthCmp.health < Data.playerData.ship.health)
            healthCmp.health += regenCmp.amount
    }
}