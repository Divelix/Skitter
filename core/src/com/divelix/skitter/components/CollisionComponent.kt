package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class CollisionComponent: Component, Pool.Poolable {
    var collisionEntity: Entity? = null

    override fun reset() {
        collisionEntity = null
    }
}