package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.data.Data
import com.divelix.skitter.gameplay.components.AmmoComponent
import ktx.ashley.allOf
import ktx.ashley.get

class AmmoSystem(interval: Float): IntervalIteratingSystem(allOf(AmmoComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
        val ammoCmp = entity[AmmoComponent.mapper]
        require(ammoCmp != null) {"Entity $entity don't have necessary components for AmmoSystem"}

        if (ammoCmp.currentAmmo < ammoCmp.maxAmmo) {
            ammoCmp.currentAmmo++
            Data.reloadTimer = 0f
        }
    }
}