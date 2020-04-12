package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.RadialComponent
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.allOf

class RadialSystem(interval: Float, val entityBuilder: EntityBuilder): IntervalIteratingSystem(allOf(RadialComponent::class).get(), interval) {
    val dirVec = Vector2(0f, 1f)
    val target = Vector2()

    override fun processEntity(entity: Entity) {
        val body = GameEngine.cmBody.get(entity).body
        val pos = body.position
        val angle = body.angle
        dirVec.setAngle(angle)
        for (i in 1..6) {
            dirVec.rotate(60f)
            target.set(pos).add(dirVec)
            entityBuilder.createEnemyBullet(entity, target)
        }
    }
}