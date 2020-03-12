package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PlayerSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    private val cmTrans = mapperFor<TransformComponent>()
    private val cmHealth = mapperFor<HealthComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()
    private val velocity = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transCmp = cmTrans.get(entity)
        val healthCmp = cmHealth.get(entity)
        val bodyCmp = cmBody.get(entity)

        PlayScreen.health = healthCmp.health
        transCmp.rotation = Data.dirVec.angle() - 90f

        velocity.set(Data.dirVec).scl(Data.playerData.ship.speed).scl(50f) // TODO hardcoded scl
        bodyCmp.body.applyForceToCenter(velocity, true)
//        bodyCmp.body.linearVelocity = velocity

    }
}