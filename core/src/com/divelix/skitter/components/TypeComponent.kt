package com.divelix.skitter.components

import com.badlogic.ashley.core.Component

class TypeComponent: Component {
    companion object {
        const val PLAYER: Short = 1
        const val ENEMY: Short = 2
        const val PLAYER_BULLET: Short = 4
        const val ENEMY_BULLET: Short = 8
        const val OBSTACLE: Short = 16
        const val SPAWN: Short = 32
        const val PUDDLE: Short = 64
        const val AGENT_SENSOR: Short = 128
    }
    var type: Short? = null
}