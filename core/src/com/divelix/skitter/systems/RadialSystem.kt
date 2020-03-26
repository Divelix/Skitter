package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.components.RadialComponent
import ktx.ashley.allOf

class RadialSystem(interval: Float): IntervalIteratingSystem(allOf(RadialComponent::class).get(), interval) {

    override fun processEntity(entity: Entity?) {
        println("Radial trigger - $entity")
    }
}