package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.DynamicData
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.MouseComponent
import com.divelix.skitter.components.PlayerComponent
import com.divelix.skitter.components.TransformComponent

class PlayerSystem(val dynamicData: DynamicData): IteratingSystem(Family.all(PlayerComponent::class.java).get()) {
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)
    private val cmMouse = ComponentMapper.getFor(MouseComponent::class.java)
    private val targetPos = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transCmp = cmTrans.get(entity)
        val bodyCmp = cmBody.get(entity)
        val mouseCmp = cmMouse.get(entity)
        if (!dynamicData.dirVec.isZero) transCmp.rotation = dynamicData.dirVec.angle() - 90f
        targetPos.set(bodyCmp.body.position).add(dynamicData.dirVec)
        mouseCmp.mouseJoint.target = targetPos
    }
}