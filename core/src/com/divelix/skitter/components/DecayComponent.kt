package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DecayComponent(val damage: Float = 2f): Component, Pool.Poolable {
    override fun reset() {}
}