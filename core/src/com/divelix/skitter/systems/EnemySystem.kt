package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class EnemySystem: IteratingSystem(allOf(EnemyComponent::class).get()) {
    private val cmBody = mapperFor<B2dBodyComponent>()
    private val cmHealth = mapperFor<HealthComponent>()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val bodyCmp = cmBody.get(entity)
        val healthCmp = cmHealth.get(entity)

        if (healthCmp.health <= 0f && !bodyCmp.isDead) {
            bodyCmp.isDead = true
            Data.enemiesCount--
            Data.score += 100
        }
    }
}