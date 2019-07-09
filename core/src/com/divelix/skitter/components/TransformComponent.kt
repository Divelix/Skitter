package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class TransformComponent: Component, Pool.Poolable {
    val position = Vector3()
    val size = Vector2()
    var rotation = 0f
    var isHidden = false //TODO move to render component

    override fun reset() {
        rotation = 0f
        isHidden = false
    }
}