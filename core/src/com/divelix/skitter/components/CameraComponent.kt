package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Pool

class CameraComponent: Component, Pool.Poolable {
    lateinit var camera: OrthographicCamera
    var needCenter = true

    override fun reset() {
        camera.position.set(0f, 0f, 0f)
        needCenter = true
    }
}