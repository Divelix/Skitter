package com.divelix.skitter.components

import com.badlogic.ashley.core.Component

class TypeComponent: Component {
    companion object {
        const val PLAYER: Short = 1
        const val ENEMY: Short = 2
        const val BULLET: Short = 4
        const val OBSTACLE: Short = 8
        const val SPAWN: Short = 16
        const val PUDDLE: Short = 32
    }
    var type: Short? = null
}