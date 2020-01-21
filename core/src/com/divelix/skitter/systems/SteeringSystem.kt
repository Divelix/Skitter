package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.SteerComponent

class SteeringSystem: IteratingSystem(Family.all(SteerComponent::class.java).get()) {
    private val cmMove = ComponentMapper.getFor(SteerComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val moveCmp = cmMove.get(entity)
        val bodyCmp = cmBody.get(entity)

//        bodyCmp.body.setTransform(bodyCmp.body.position, moveCmp.steering.angleRad())
//        bodyCmp.body.applyForceToCenter(moveCmp.steering, true)
        bodyCmp.body.applyForce(moveCmp.steering, bodyCmp.body.getWorldPoint(Vector2(0f, 0.75f)), true)
    }

}