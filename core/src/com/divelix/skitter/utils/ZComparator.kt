package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.TransformComponent
import ktx.ashley.get

class ZComparator : Comparator<Entity> {
    override fun compare(entityA: Entity, entityB: Entity): Int {
        val az = entityA[TransformComponent.mapper]!!.position.z
        val bz = entityB[TransformComponent.mapper]!!.position.z
        var res = 0
        if (az > bz) {
            res = 1
        } else if (az < bz) {
            res = -1
        }
        return res
    }
}