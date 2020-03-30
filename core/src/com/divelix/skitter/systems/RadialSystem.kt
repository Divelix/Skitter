package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.RadialComponent
import com.divelix.skitter.components.TransformComponent
import com.divelix.skitter.screens.PlayScreen
import com.divelix.skitter.utils.EntityBuilder
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class RadialSystem(interval: Float, val entityBuilder: EntityBuilder): IntervalIteratingSystem(allOf(RadialComponent::class).get(), interval) {
    private val cmBody = mapperFor<B2dBodyComponent>()
    val dirVec = Vector2(0f, 1f)
    val target = Vector2()

    override fun processEntity(entity: Entity) {
        if (PlayScreen.isPaused) return
        val body = cmBody.get(entity).body
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