package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.gameplay.components.ProjectileComponent
import ktx.ashley.allOf
import ktx.ashley.get

class ProjectileSystem: IteratingSystem(allOf(ProjectileComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val projectileCmp = entity[ProjectileComponent.mapper]!!

        projectileCmp.timer -= deltaTime

        if (projectileCmp.timer <= 0f) engine.removeEntity(entity)
    }
}