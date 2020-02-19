package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.JumperComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class JumperSystem: IntervalIteratingSystem(allOf(JumperComponent::class).get(), 3f) {
    val cmBody = mapperFor<B2dBodyComponent>()
    val force = 5000f
    val direction = Vector2(force, 0f)

    override fun processEntity(entity: Entity?) {
        val bodyCmp = cmBody.get(entity)
        direction.rotateRad(MathUtils.random(MathUtils.PI2))
        bodyCmp.body.applyForceToCenter(direction, true)
    }
}