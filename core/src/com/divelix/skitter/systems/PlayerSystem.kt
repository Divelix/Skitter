package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen

class PlayerSystem: IteratingSystem(Family.all(PlayerComponent::class.java).get()) {
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)
    private val cmMouse = ComponentMapper.getFor(MouseComponent::class.java)
    private val cmHealth = ComponentMapper.getFor(HealthComponent::class.java)
    private val targetPos = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transCmp = cmTrans.get(entity)
        val bodyCmp = cmBody.get(entity)
        val mouseCmp = cmMouse.get(entity)
        val healthCmp = cmHealth.get(entity)
        PlayScreen.playerHealth = healthCmp.health
        transCmp.rotation = Data.dynamicData.dirVec.angle() - 90f
        targetPos.set(bodyCmp.body.position).add(Data.dynamicData.dirVec)
        mouseCmp.mouseJoint.target = targetPos
    }
}