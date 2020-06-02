package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class EnemyComponent: Component, Pool.Poolable {
//    var damage = 1f //TODO use this damage in bullet (or not?)

    override fun reset() {}

    companion object {
        val mapper = mapperFor<EnemyComponent>()
    }
}