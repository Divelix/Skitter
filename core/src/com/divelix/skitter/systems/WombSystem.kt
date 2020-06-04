package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.components.WombComponent
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.allOf

class WombSystem(interval: Float, val entityBuilder: EntityBuilder): IntervalIteratingSystem(allOf(WombComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
        for (i in 1..2) entityBuilder.createKid(entity)
    }
}