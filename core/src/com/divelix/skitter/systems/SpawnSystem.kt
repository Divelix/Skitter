package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.MathUtils
import com.divelix.skitter.Constants
import com.divelix.skitter.utils.EntityBuilder

class SpawnSystem(interval: Float, val entityBuilder: EntityBuilder, val playerEntity: Entity): IntervalSystem(interval) {
    override fun updateInterval() {
        val x = MathUtils.random(0, Constants.WIDTH.toInt()).toFloat()
        val y = MathUtils.random(0, Constants.WIDTH.toInt()).toFloat()
        entityBuilder.createEnemy(x, y, 2f, playerEntity)
    }
}