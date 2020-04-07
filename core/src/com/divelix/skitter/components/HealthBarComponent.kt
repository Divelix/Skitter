package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

class HealthBarComponent: Component, Pool.Poolable {
    val height = 0.2f
    var maxValue = 100f

    override fun reset() {}
}