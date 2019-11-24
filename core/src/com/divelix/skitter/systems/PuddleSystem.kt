package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.components.PuddleComponent

// TODO delete this or do smth usefull
class PuddleSystem: IteratingSystem(Family.all(PuddleComponent::class.java).get()) {
    private val cmPuddle = ComponentMapper.getFor(PuddleComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val puddleCmp = cmPuddle.get(entity)
    }
}