package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class ClickableComponent: Component, Pool.Poolable {
    val circle = Circle()

    override fun reset() {}

    companion object {
        val mapper = mapperFor<ClickableComponent>()
    }
}