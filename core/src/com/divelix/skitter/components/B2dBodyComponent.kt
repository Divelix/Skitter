package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class B2dBodyComponent: Component, Pool.Poolable {
    lateinit var body: Body

    override fun reset() {
        body.world.destroyBody(body)
    }
}