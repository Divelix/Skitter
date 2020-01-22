package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class CollisionComponent: Component, Pool.Poolable {
    var collisionEntity: Entity? = null
    var isBeginContact = true
    var collidedCategoryBits: Short = 0 // had to add it to process agent sensor (2 bodies with different category bits in one entity)

    override fun reset() {
        collisionEntity = null
        isBeginContact = true
    }
}