package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class EnemyComponent: Component, Pool.Poolable {
    var health = 100
    var damage = 10

    override fun reset() {
        health = 100
    }
}