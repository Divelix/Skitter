package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.data.Data
import com.divelix.skitter.gameplay.components.EnemyComponent
import com.divelix.skitter.gameplay.components.HealthComponent
import com.divelix.skitter.gameplay.components.PlayerComponent
import com.divelix.skitter.ui.hud.Hud
import com.divelix.skitter.gameplay.LevelManager
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.collections.*

class HealthSystem(
        private val hud: Hud
): IteratingSystem(allOf(HealthComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val healthCmp = entity[HealthComponent.mapper]!!

        if (healthCmp.currentHealth <= 0f) {
            if (entity.has(EnemyComponent.mapper)) {
                LevelManager.enemiesCount--
                Data.score += 100
                val enemyType = entity[EnemyComponent.mapper]!!.type!!
                if (enemyType in Data.matchHistory) {
                    Data.matchHistory[enemyType] += 1
                } else {
                    Data.matchHistory[enemyType] = 1
                }
                engine.removeEntity(entity)
            } else if (entity.has(PlayerComponent.mapper)) {
                hud.showGameOverWindow()
            }
        }
    }
}