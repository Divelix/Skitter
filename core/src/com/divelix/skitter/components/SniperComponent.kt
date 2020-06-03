package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class SniperComponent: Component, Pool.Poolable {

    var firingTimer = 0f

    override fun reset() {
        firingTimer = 0f
    }

    companion object {
        val mapper = mapperFor<SniperComponent>()
        const val firingPeriod = 1.5f
    }
}