package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Constants
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.BindComponent
import com.divelix.skitter.components.BulletComponent

class BulletSystem: IteratingSystem(Family.all(BulletComponent::class.java).get()) {
    private val cmBullet = ComponentMapper.getFor(BulletComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val bulletCmp = cmBullet.get(entity)
        val bodyCmp = cmBody.get(entity)

        bulletCmp.timer -= deltaTime

        if (bulletCmp.timer <= 0f)
            bulletCmp.isDead = true

        if (bulletCmp.isDead)
            bodyCmp.isDead = true
    }
}