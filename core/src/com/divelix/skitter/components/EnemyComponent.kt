package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class EnemyComponent: Component, Pool.Poolable {
//    var damage = 1f //TODO use this damage in buttel (or not?)

    override fun reset() {}
}