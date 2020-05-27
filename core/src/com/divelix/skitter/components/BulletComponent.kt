package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class BulletComponent: Component, Pool.Poolable {
    var timer = 10f
    var damage = 0f

    override fun reset() {
        timer = 10f
        damage = 0f
    }
}