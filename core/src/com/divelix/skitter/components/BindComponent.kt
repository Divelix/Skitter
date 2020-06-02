package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class BindComponent: Component, Pool.Poolable {
    lateinit var entity: Entity

    override fun reset() {}

    companion object {
        val mapper = mapperFor<BindComponent>()
    }
}