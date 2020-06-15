package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TextureComponent : Component, Pool.Poolable {
    val sprite = Sprite()

    override fun reset() {
        sprite.texture = null
    }

    companion object {
        val mapper = mapperFor<TextureComponent>()
    }
}