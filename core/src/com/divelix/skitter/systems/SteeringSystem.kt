package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.SteerComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class SteeringSystem: IteratingSystem(allOf(SteerComponent::class).get()) {
    private val cmSteer = mapperFor<SteerComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val steerCmp = cmSteer.get(entity)
        val bodyCmp = cmBody.get(entity)

        bodyCmp.body.applyForce(steerCmp.steeringForce, bodyCmp.body.getWorldPoint(steerCmp.steeringPoint), true)
    }

}