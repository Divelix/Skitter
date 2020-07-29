package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.gameplay.components.B2dBodyComponent
import com.divelix.skitter.gameplay.components.PlayerComponent
import com.divelix.skitter.gameplay.components.SniperComponent
import com.divelix.skitter.gameplay.components.TowerComponent
import com.divelix.skitter.gameplay.components.TransformComponent
import com.divelix.skitter.gameplay.components.VisionComponent
import com.divelix.skitter.gameplay.EntityBuilder
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has

class SniperSystem(val entityBuilder: EntityBuilder): IteratingSystem(allOf(SniperComponent::class).get()) {
    private val targetPos = Vector2()
    private val diffVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val sniperCmp = entity[SniperComponent.mapper]!!
        val visionCmp = entity[VisionComponent.mapper]!!
        val playerEntity = visionCmp.visibleEntities.singleOrNull { it.has(PlayerComponent.mapper) } ?: return
        val playerPos = playerEntity[TransformComponent.mapper]!!.position
        val body = entity[B2dBodyComponent.mapper]!!.body
        targetPos.set(playerPos.x, playerPos.y)
        entity[TowerComponent.mapper]!!.angle = diffVec.set(targetPos).sub(body.position).angle()

        if (sniperCmp.firingTimer > 0f) {
            sniperCmp.firingTimer -= deltaTime
        } else {
            entityBuilder.createEnemyBullet(entity, targetPos)
            sniperCmp.firingTimer = SniperComponent.firingPeriod
        }

    }

    companion object {
        const val TAG = "SniperSystem"
    }
}