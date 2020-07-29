package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TransformComponent: Component, Pool.Poolable, Comparable<TransformComponent> {
    val position = Vector3()
    val size = Vector2()
    val origin = Vector2()
    var rotation = 0f

    override fun reset() {
        position.setZero()
        size.setZero()
        origin.setZero()
        rotation = 0f
    }

    override fun compareTo(other: TransformComponent): Int {
        val zDiff = position.z - other.position.z
//        return (if (zDiff==0f) position.y - other.position.y else zDiff).toInt()
        return zDiff.toInt()
    }

    companion object {
        val mapper = mapperFor<TransformComponent>()
    }
}