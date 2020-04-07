package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HealthComponent: Component, Pool.Poolable {
    var isIntHp = false
    var health = 0f

    override fun reset() {}
}