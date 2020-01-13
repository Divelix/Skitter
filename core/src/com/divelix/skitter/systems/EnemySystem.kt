package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.components.*

class EnemySystem: IteratingSystem(Family.all(EnemyComponent::class.java).get()) {
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)
    private val cmHealth = ComponentMapper.getFor(HealthComponent::class.java)

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