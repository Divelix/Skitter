package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class HealthBarComponent: Component, Pool.Poolable {
    val height = 0.2f
    var maxValue = 100f
    private val redPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
        setColor(1f, 0f, 0f, 1f)
        fill()
    }
    val sprite = Sprite(Texture(redPixel))

    override fun reset() {}

    companion object {
        val mapper = mapperFor<HealthBarComponent>()
    }
}