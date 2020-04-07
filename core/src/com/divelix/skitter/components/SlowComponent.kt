package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class SlowComponent(var slowRate: Float = 0.5f): Component, Pool.Poolable {
    override fun reset() {}
}