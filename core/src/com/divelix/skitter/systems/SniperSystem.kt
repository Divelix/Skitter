package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.SniperComponent
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.allOf
import ktx.ashley.has
import ktx.math.minus

class SniperSystem(interval: Float, val entityBuilder: EntityBuilder): IntervalIteratingSystem(allOf(SniperComponent::class).get(), interval) {
    private val targetPos = Vector2()

    override fun processEntity(entity: Entity) {
        val visionCmp = GameEngine.cmVision.get(entity)
        val playerEntity = visionCmp.visibleEntities.singleOrNull { it.has(GameEngine.cmPlayer) } ?: return
        val playerPos = GameEngine.cmTransform.get(playerEntity).position
//        val body = GameEngine.cmBody.get(entity).body

        targetPos.set(playerPos.x, playerPos.y)
        entityBuilder.createEnemyBullet(entity, targetPos)
//        val posDiff = targetPos - body.position
//        val angleDiff = posDiff.angleRad() - body.angle
    }

    companion object {
        const val TAG = "SniperSystem"
    }
}