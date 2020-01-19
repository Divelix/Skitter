package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.*

class LoverSystem: IteratingSystem(Family.all(LoverComponent::class.java).get()) {
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)
    private val cmBind = ComponentMapper.getFor(BindComponent::class.java)
    private val cmMove = ComponentMapper.getFor(SteerComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)
    private val loverPos = Vector2()
    private val targetPos = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transCmp = cmTrans.get(entity)
        val bindCmp = cmBind.get(entity)
        val moveCmp = cmMove.get(entity)
        val bodyCmp = cmBody.get(entity)
        val targetTransCmp = cmTrans.get(bindCmp.entity)

        loverPos.set(transCmp.position.x, transCmp.position.y)
        targetPos.set(targetTransCmp.position.x, targetTransCmp.position.y)
        // steering = desired - velocity
        val desired = targetPos.sub(loverPos).setLength(moveCmp.maxSpeed)
        val steering = desired.sub(bodyCmp.body.linearVelocity).limit(moveCmp.maxForce)
        moveCmp.steering.set(steering)
    }
}