package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.BindComponent
import com.divelix.skitter.components.EnemyComponent
import com.divelix.skitter.components.TransformComponent

class EnemySystem: IteratingSystem(Family.all(EnemyComponent::class.java).get()) {
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)
    private val cmBody = ComponentMapper.getFor(B2dBodyComponent::class.java)
    private val cmBind = ComponentMapper.getFor(BindComponent::class.java)
    private val speed = 5f
    var time = 0f

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val transCmp = cmTrans.get(entity)
        val bodyCmp = cmBody.get(entity)
        val bindCmp = cmBind.get(entity)
        val playerPos = bindCmp.entity.getComponent(B2dBodyComponent::class.java).body.position
        val enemyPos = Vector2(transCmp.position.x, transCmp.position.y)
        bodyCmp.body.linearVelocity = playerPos.sub(enemyPos).nor().scl(speed)

//        time += deltaTime
//        bodyCmp.body.linearVelocity = Vector2(reload_speed * MathUtils.sin(time), -5f)
    }
}