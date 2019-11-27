package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.MoveComponent

class MovementSystem: IteratingSystem(Family.all(MoveComponent::class.java).get()) {
    private val cmMove = ComponentMapper.getFor(MoveComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)
    private val velocity = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val moveCmp = cmMove.get(entity)
        val bodyCmp = cmBody.get(entity)
        velocity.set(moveCmp.direction).scl(moveCmp.speed)
        bodyCmp.body.linearVelocity = velocity
    }

}