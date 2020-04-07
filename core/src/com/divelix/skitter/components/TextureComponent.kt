package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

class TextureComponent : Component, Pool.Poolable {
    var region: TextureRegion? = null

    override fun reset() {}
}