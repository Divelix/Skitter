package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.*
import com.divelix.skitter.utils.LevelManager
import ktx.ashley.allOf
import ktx.ashley.has

class HealthSystem: IteratingSystem(allOf(HealthComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val healthCmp = GameEngine.cmHealth.get(entity)

        if (healthCmp.health <= 0f) {
            if (entity.has(GameEngine.cmEnemy)) {
                LevelManager.enemiesCount--
                Data.score += 100
            }
            engine.removeEntity(entity)
        }
    }
}