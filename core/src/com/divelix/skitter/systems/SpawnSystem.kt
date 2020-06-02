package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.SpawnComponent
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.sqrt

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