package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.data.Data
import com.divelix.skitter.gameplay.components.HealthComponent
import com.divelix.skitter.gameplay.components.RegenerationComponent
import ktx.ashley.allOf
import ktx.ashley.get

class RegenerationSystem(interval: Float): IntervalIteratingSystem(allOf(RegenerationComponent::class, HealthComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
        val regenCmp = entity[RegenerationComponent.mapper]!!
        val healthCmp = entity[HealthComponent.mapper]!!

        if (healthCmp.health < Data.PLAYER_DATA_OLD.ship.health)
            healthCmp.health += regenCmp.amount
    }
}