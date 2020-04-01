package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.BulletComponent
import com.divelix.skitter.screens.PlayScreen
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class BulletSystem: IteratingSystem(allOf(BulletComponent::class).get()) {
    private val cmBullet = mapperFor<BulletComponent>()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val bulletCmp = cmBullet.get(entity)

        bulletCmp.timer -= deltaTime

        if (bulletCmp.timer <= 0f) engine.removeEntity(entity)
    }
}