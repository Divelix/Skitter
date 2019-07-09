package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent: Component, Pool.Poolable {
    var health = 100

    override fun reset() {
        health = 100
    }
}
