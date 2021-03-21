package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TileComponent: Component, Pool.Poolable {
    val tile = TiledDrawable()

    override fun reset() {
        tile.region = null
    }

    companion object {
        val mapper = mapperFor<TileComponent>()
    }
}