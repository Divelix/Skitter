package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.MathUtils
import com.divelix.skitter.Constants
import com.divelix.skitter.components.CollisionComponent
import com.divelix.skitter.components.SpawnComponent
import com.divelix.skitter.utils.EntityBuilder
import kotlin.math.sqrt

class SpawnSystem(interval: Float, val entityBuilder: EntityBuilder, val playerEntity: Entity): IntervalIteratingSystem(Family.all(SpawnComponent::class.java).get(), interval) {
    private val cmSpawn = ComponentMapper.getFor(SpawnComponent::class.java)

    override fun processEntity(entity: Entity?) {
        val spawnCmp = cmSpawn.get(entity)
        val a = MathUtils.random() * MathUtils.PI2
        val r = spawnCmp.circle.radius * sqrt(MathUtils.random())
        val x = spawnCmp.circle.x + r * MathUtils.cos(a)
        val y = spawnCmp.circle.y + r * MathUtils.sin(a)
        entityBuilder.createEnemy(x, y, 2f, playerEntity)
    }
}