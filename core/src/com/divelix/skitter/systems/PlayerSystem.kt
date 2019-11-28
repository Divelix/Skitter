package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen

class PlayerSystem: IteratingSystem(Family.all(PlayerComponent::class.java).get()) {
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)
    private val cmHealth = ComponentMapper.getFor(HealthComponent::class.java)
    private val cmMove = ComponentMapper.getFor(MoveComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transCmp = cmTrans.get(entity)
        val healthCmp = cmHealth.get(entity)
        val moveCmp = cmMove.get(entity)

        PlayScreen.health = healthCmp.health
        transCmp.rotation = Data.dynamicData.dirVec.angle() - 90f
        moveCmp.direction.set(Data.dynamicData.dirVec)
    }
}