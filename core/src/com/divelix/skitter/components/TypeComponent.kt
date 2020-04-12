package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import kotlin.experimental.or

class TypeComponent: Component, Pool.Poolable {
    var type: Short? = null

    override fun reset() {
        type = null
    }

    companion object {
        const val VISION_SENSOR: Short = 1
        const val PLAYER_BULLET: Short = 2
        const val ENEMY_BULLET: Short = 4
        const val PLAYER: Short = 8
        const val ENEMY: Short = 16
        const val SPAWN: Short = 32
        const val PUDDLE: Short = 64
        const val OBSTACLE: Short = 128
        const val DOOR: Short = 256

        val VISION_SENSOR_MB = PLAYER or ENEMY or OBSTACLE
        val PLAYER_BULLET_MB = ENEMY or OBSTACLE
        val ENEMY_BULLET_MB = PLAYER or OBSTACLE
        val PLAYER_MB = ENEMY or ENEMY_BULLET or OBSTACLE or SPAWN or PUDDLE or VISION_SENSOR or DOOR
        val ENEMY_MB = PLAYER or ENEMY or PLAYER_BULLET or OBSTACLE
    }
}