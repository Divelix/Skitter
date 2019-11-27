package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Constants
import com.divelix.skitter.components.MoveComponent
import com.divelix.skitter.components.SlowComponent

class SlowSystem: IteratingSystem(Family.all(SlowComponent::class.java).get()) {
    private val cmSlow = ComponentMapper.getFor(SlowComponent::class.java)
    private val cmMove = ComponentMapper.getFor(MoveComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val slowCmp = cmSlow.get(entity)
        val moveCmp = cmMove.get(entity)
        moveCmp.speed = Constants.ENEMY_SPEED * slowCmp.slowRate
    }
}