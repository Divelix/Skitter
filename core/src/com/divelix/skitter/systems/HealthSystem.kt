package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import com.divelix.skitter.utils.LevelManager
import ktx.ashley.allOf
import ktx.ashley.has
import ktx.ashley.mapperFor

class HealthSystem: IteratingSystem(allOf(HealthComponent::class).get()) {
    private val cmHealth = mapperFor<HealthComponent>()
    private val cmEnemy = mapperFor<EnemyComponent>()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val healthCmp = cmHealth.get(entity)

        if (healthCmp.health <= 0f) {
            if (entity.has(cmEnemy)) {
                LevelManager.enemiesCount--
                Data.score += 100
            }
            engine.removeEntity(entity)
        }
    }
}