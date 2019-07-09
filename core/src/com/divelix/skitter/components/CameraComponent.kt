package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.Constants

class CameraComponent: Component, Pool.Poolable {
    val camera = OrthographicCamera()

    override fun reset() {
        camera.position.set(0f, 0f, 0f)
    }
}