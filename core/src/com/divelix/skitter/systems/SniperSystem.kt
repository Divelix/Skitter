package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.BindComponent
import com.divelix.skitter.components.SniperComponent
import com.divelix.skitter.components.TransformComponent
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.EntityBuilder

class SniperSystem(interval: Float, val entityBuilder: EntityBuilder): IntervalIteratingSystem(Family.all(SniperComponent::class.java).get(), interval) {
    private val cmBind = ComponentMapper.getFor(BindComponent::class.java)
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)
    private val targetPos = Vector2()

    override fun processEntity(entity: Entity?) {
        if (PlayScreen.isPaused) return
        val bindCmp = cmBind.get(entity)
        val targetTransCmp = cmTrans.get(bindCmp.entity)

        targetPos.set(targetTransCmp.position.x, targetTransCmp.position.y)
        entityBuilder.createBullet(entity!!, targetPos)
    }
}