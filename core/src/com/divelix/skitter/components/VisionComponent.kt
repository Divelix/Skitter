package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectSet
import com.badlogic.gdx.utils.Pool

class VisionComponent: Component, Pool.Poolable {
    val visibleEntities = ObjectSet<Entity>(5)

    override fun reset() {}
}