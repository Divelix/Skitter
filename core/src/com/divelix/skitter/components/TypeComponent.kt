package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import kotlin.experimental.or

class TypeComponent: Component {
    companion object {
        const val AGENT_SENSOR: Short = 1
        const val PLAYER_BULLET: Short = 2
        const val ENEMY_BULLET: Short = 4
        const val PLAYER: Short = 8
        const val AGENT: Short = 16
        const val SPAWN: Short = 32
        const val PUDDLE: Short = 64
        const val OBSTACLE: Short = 128
        const val DOOR: Short = 256

        val AGENT_SENSOR_MB = PLAYER or AGENT or OBSTACLE
        val PLAYER_BULLET_MB = AGENT or OBSTACLE
        val ENEMY_BULLET_MB = PLAYER or OBSTACLE
        val PLAYER_MB = AGENT or ENEMY_BULLET or OBSTACLE or SPAWN or PUDDLE or AGENT_SENSOR or DOOR
    }
    var type: Short? = null
}