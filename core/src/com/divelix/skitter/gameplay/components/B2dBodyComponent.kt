package com.divelix.skitter.gameplay.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class B2dBodyComponent: Component, Pool.Poolable {
    lateinit var body: Body

    override fun reset() {
        body.world.destroyBody(body)
    }

    companion object {
        val mapper = mapperFor<B2dBodyComponent>()
    }
}