package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.gameplay.components.WombComponent
import com.divelix.skitter.gameplay.EntityBuilder
import ktx.ashley.allOf

class WombSystem(
        interval: Float,
        val entityBuilder: EntityBuilder
): IntervalIteratingSystem(allOf(WombComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
        for (i in 1..2) entityBuilder.createKid(entity)
    }
}