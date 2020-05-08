package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.divelix.skitter.GameEngine

class ZComparator : Comparator<Entity> {
    override fun compare(entityA: Entity, entityB: Entity): Int {
        val az = GameEngine.cmTransform.get(entityA).position.z
        val bz = GameEngine.cmTransform.get(entityB).position.z
        var res = 0
        if (az > bz) {
            res = 1
        } else if (az < bz) {
            res = -1
        }
        return res
    }
}