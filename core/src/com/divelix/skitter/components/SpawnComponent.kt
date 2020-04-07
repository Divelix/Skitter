package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.utils.Pool

class SpawnComponent: Component, Pool.Poolable {
    val circle = Circle()

    override fun reset() {}
}