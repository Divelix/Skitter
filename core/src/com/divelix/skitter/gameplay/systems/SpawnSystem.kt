package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.divelix.skitter.gameplay.components.SpawnComponent
import com.divelix.skitter.gameplay.EntityBuilder
import ktx.ashley.allOf

class SpawnSystem(interval: Float, val entityBuilder: EntityBuilder, val playerEntity: Entity): IntervalIteratingSystem(allOf(SpawnComponent::class).get(), interval) {

    override fun processEntity(entity: Entity) {
//        val spawnCmp = entity[SpawnComponent.mapper]!!
//        val a = MathUtils.random() * MathUtils.PI2
//        val r = spawnCmp.circle.radius * sqrt(MathUtils.random())
//        val x = spawnCmp.circle.x + r * MathUtils.cos(a)
//        val y = spawnCmp.circle.y + r * MathUtils.sin(a)
//        entityBuilder.createLover(x, y, playerEntity)
    }
}