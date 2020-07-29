package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.gameplay.components.BulletComponent
import ktx.ashley.allOf
import ktx.ashley.get

class BulletSystem: IteratingSystem(allOf(BulletComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bulletCmp = entity[BulletComponent.mapper]!!

        bulletCmp.timer -= deltaTime

        if (bulletCmp.timer <= 0f) engine.removeEntity(entity)
    }
}