package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.divelix.skitter.components.AgentComponent
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.SteerComponent

class AgentSystem: IteratingSystem(Family.all(AgentComponent::class.java).get()) {
    private val cmSteer = ComponentMapper.getFor(SteerComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val steerCmp = cmSteer.get(entity)
        val bodyCmp = cmBody.get(entity)
    }
}