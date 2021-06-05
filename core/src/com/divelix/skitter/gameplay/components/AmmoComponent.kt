package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class AmmoComponent: Component, Pool.Poolable {
    var maxAmmo = 0
    var currentAmmo = 0

    override fun reset() {
        currentAmmo = maxAmmo
    }

    companion object {
        val mapper = mapperFor<AmmoComponent>()
    }
}