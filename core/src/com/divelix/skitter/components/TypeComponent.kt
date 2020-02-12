package com.divelix.skitter.components

import com.badlogic.ashley.core.Component

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
    }
    var type: Short? = null
}