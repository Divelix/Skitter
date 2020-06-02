package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.DamageLabelComponent
import com.divelix.skitter.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class DamageLabelSystem(val camera: OrthographicCamera): IteratingSystem(allOf(DamageLabelComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val damageCmp = entity[DamageLabelComponent.mapper]!!
        val transCmp = entity[TransformComponent.mapper]!!

        for (label in damageCmp.damageLabels) {
            label.latestPos.set(transCmp.position)
            label.ecsTimer -= deltaTime
            if (label.ecsTimer <= 0f) {
                damageCmp.damageLabels.removeValue(label, true)
            }
        }
    }
}