package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.SlowComponent
import ktx.ashley.allOf

class SlowSystem: IteratingSystem(allOf(SlowComponent::class).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val slowCmp = GameEngine.cmSlow.get(entity)
        val moveCmp = GameEngine.cmSteer.get(entity)
        moveCmp.maxSpeed = Data.loverData.maxSpeed * slowCmp.slowRate
    }
}