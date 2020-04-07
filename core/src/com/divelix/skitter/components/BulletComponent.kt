package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class BulletComponent: Component, Pool.Poolable {
    var timer = 5f
    var damage = 0f

    override fun reset() {
        timer = 5f
        damage = 0f
    }
}