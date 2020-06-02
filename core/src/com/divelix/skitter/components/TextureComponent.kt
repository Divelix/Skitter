package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TextureComponent : Component, Pool.Poolable {
    lateinit var region: TextureRegion

    override fun reset() {}

    companion object {
        val mapper = mapperFor<TextureComponent>()
    }
}