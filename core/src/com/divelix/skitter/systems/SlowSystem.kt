package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.SteerComponent
import com.divelix.skitter.components.SlowComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class SlowSystem: IteratingSystem(allOf(SlowComponent::class).get()) {
    private val cmSlow = mapperFor<SlowComponent>()
    private val cmMove = mapperFor<SteerComponent>()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val slowCmp = cmSlow.get(entity)
        val moveCmp = cmMove.get(entity)
        moveCmp.maxSpeed = Data.loverData.maxSpeed * slowCmp.slowRate
    }
}