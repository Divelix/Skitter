package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.BindComponent
import com.divelix.skitter.components.SniperComponent
import com.divelix.skitter.components.TransformComponent
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class SniperSystem(interval: Float, val entityBuilder: EntityBuilder): IntervalIteratingSystem(allOf(SniperComponent::class).get(), interval) {
    private val cmBind = mapperFor<BindComponent>()
    private val cmTrans = mapperFor<TransformComponent>()
    private val targetPos = Vector2()

    override fun processEntity(entity: Entity?) {
        val bindCmp = cmBind.get(entity)
        val targetTransCmp = cmTrans.get(bindCmp.entity)

        targetPos.set(targetTransCmp.position.x, targetTransCmp.position.y)
        entityBuilder.createEnemyBullet(entity!!, targetPos)
    }
}