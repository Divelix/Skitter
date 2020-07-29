package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.data.Enemy
import ktx.ashley.mapperFor

class EnemyComponent: Component, Pool.Poolable {
    var type: Enemy? = null

    override fun reset() {
        type = null
    }

    companion object {
        val mapper = mapperFor<EnemyComponent>()
    }
}