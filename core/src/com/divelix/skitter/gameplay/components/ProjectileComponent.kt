package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class ProjectileComponent: Component, Pool.Poolable {
    var timer = 10f
    var damage = 0f

    override fun reset() {
        timer = 10f
        damage = 0f
    }

    companion object {
        val mapper = mapperFor<ProjectileComponent>()
    }
}