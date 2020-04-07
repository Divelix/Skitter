package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class BindComponent: Component, Pool.Poolable {
    lateinit var entity: Entity

    override fun reset() {}
}