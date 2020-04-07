package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RegenerationComponent(var amount: Float = 1f): Component, Pool.Poolable {
    override fun reset() {}
}