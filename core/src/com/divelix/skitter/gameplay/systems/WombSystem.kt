package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.gameplay.components.WombComponent
import com.divelix.skitter.gameplay.EntityBuilder
import com.divelix.skitter.gameplay.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class WombSystem(
        interval: Float,
        private val entityBuilder: EntityBuilder
): IntervalIteratingSystem(allOf(WombComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
        val pos = entity[TransformComponent.mapper]!!.position
        for (i in 1..2) entityBuilder.createKid(pos.x, pos.y)
    }
}