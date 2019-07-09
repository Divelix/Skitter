package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.Constants

class BulletComponent: Component, Pool.Poolable {
    val width = 0.1f
    val height = 0.5f
    val speed = Constants.BULLET_SPEED
    var isDead = false

    override fun reset() {
        isDead = false
    }
}