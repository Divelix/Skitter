package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.Constants

class BulletComponent: Component, Pool.Poolable {
    var timer = 5f
    var isDead = false

    override fun reset() {
        timer = 5f
        isDead = false
    }
}