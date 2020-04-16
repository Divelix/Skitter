package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.PlayScreen
import ktx.ashley.allOf
import ktx.ashley.has

class PlayerSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    private val force = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transCmp = GameEngine.cmTransform.get(entity)
        val healthCmp = GameEngine.cmHealth.get(entity)
        val bodyCmp = GameEngine.cmBody.get(entity)

        PlayScreen.health = healthCmp.health
        transCmp.rotation = Data.dirVec.angle() - 90f

        force.set(Data.dirVec).scl(Data.playerData.ship.speed).scl(50f) // TODO hardcoded scl
        bodyCmp.body.applyForceToCenter(force, true)
    }
}