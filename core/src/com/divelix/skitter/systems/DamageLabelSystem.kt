package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.DamageLabelComponent
import ktx.ashley.allOf

class DamageLabelSystem(val camera: OrthographicCamera): IteratingSystem(allOf(DamageLabelComponent::class).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val damageCmp = GameEngine.cmDmgLabel.get(entity)
        val transCmp = GameEngine.cmTransform.get(entity)

        for (label in damageCmp.damageLabels) {
            label.latestPos.set(transCmp.position)
            label.ecsTimer -= deltaTime
            if (label.ecsTimer <= 0f) {
                damageCmp.damageLabels.removeValue(label, true)
            }
        }
    }
}