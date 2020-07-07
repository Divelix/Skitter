package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.LevelManager
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.collections.*

class HealthSystem(
        private val hud: Hud
): IteratingSystem(allOf(HealthComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val healthCmp = entity[HealthComponent.mapper]!!

        if (healthCmp.health <= 0f) {
            if (entity.has(EnemyComponent.mapper)) {
                LevelManager.enemiesCount--
                Data.score += 100
                val enemyType = entity[EnemyComponent.mapper]!!.type!!
                if (enemyType in Data.matchHistory) {
                    Data.matchHistory[enemyType] += 1
                } else {
                    Data.matchHistory[enemyType] = 1
                }
            } else if (entity.has(PlayerComponent.mapper)) {
                GameEngine.slowRate = 10f
                hud.showGameOverWindow()
            }
            engine.removeEntity(entity)
        }
    }
}