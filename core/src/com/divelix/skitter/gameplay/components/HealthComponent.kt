package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class HealthComponent: Component, Pool.Poolable {
    var isIntHp = false
    var health = 0f

    override fun reset() {
        isIntHp = false
        health = 0f
    }

    companion object {
        val mapper = mapperFor<HealthComponent>()
    }
}