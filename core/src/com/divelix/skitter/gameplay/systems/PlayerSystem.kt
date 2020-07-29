package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.data.Data
import com.divelix.skitter.gameplay.components.B2dBodyComponent
import com.divelix.skitter.gameplay.components.HealthComponent
import com.divelix.skitter.gameplay.components.PlayerComponent
import com.divelix.skitter.gameplay.components.TransformComponent
import com.divelix.skitter.screens.PlayScreen
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    private val force = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transCmp = entity[TransformComponent.mapper]!!
        val healthCmp = entity[HealthComponent.mapper]!!
        val bodyCmp = entity[B2dBodyComponent.mapper]!!

        PlayScreen.health = healthCmp.health
        transCmp.rotation = Data.dirVec.angle() - 90f

        force.set(Data.dirVec).scl(Data.playerData.ship.speed).scl(50f) // TODO hardcoded scl
        bodyCmp.body.applyForceToCenter(force, true)
    }
}