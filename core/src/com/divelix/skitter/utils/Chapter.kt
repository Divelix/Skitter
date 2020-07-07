package com.divelix.skitter.utils

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

data class Chapter(
        var name: String = "Chapter name",
        var levels: Array<Level> = Array()
)

data class Level(
        var size: Vector2 = Vector2(),
        var enemies: Array<EnemyBundle> = Array()
//        var obstacles: List<Obstacle> = emptyList()
)

data class EnemyBundle(
        var enemy: Enemy = Enemy.SNIPER,
        var quantity: Int = 1
)

enum class Enemy {
    AGENT,
    JUMPER,
    SNIPER,
    WOMB,
    KID,
    RADIAL
}

enum class Obstacle {
    STATIC,
    STATIC_BREAKABLE,
    DYNAMIC_BREAKABLE
}