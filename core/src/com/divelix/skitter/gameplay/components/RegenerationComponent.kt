package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class RegenerationComponent(var amount: Float = 1f): Component, Pool.Poolable {
    override fun reset() {}

    companion object {
        val mapper = mapperFor<RegenerationComponent>()
    }
}