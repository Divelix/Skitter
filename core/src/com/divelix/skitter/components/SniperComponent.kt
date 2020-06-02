package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class SniperComponent: Component, Pool.Poolable {
    override fun reset() {}

    companion object {
        val mapper = mapperFor<SniperComponent>()
    }
}