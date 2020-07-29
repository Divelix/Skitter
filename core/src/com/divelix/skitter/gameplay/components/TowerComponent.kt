package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TowerComponent: Component, Pool.Poolable {
    val sprite = Sprite()
    var angle = 0f

    override fun reset() {
        angle = 0f
    }

    companion object {
        val mapper = mapperFor<TowerComponent>()
    }
}