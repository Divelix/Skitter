package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.divelix.skitter.Data
import com.divelix.skitter.components.BindComponent
import com.divelix.skitter.components.DamageLabelComponent
import com.divelix.skitter.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import java.lang.NullPointerException

class DamageLabelSystem(val camera: OrthographicCamera): IteratingSystem(allOf(DamageLabelComponent::class).get()) {
    val cmDamage = mapperFor<DamageLabelComponent>()
    val cmBind = mapperFor<BindComponent>()
    val cmTrans = mapperFor<TransformComponent>()

    val vec3 = Vector3()
    val nextPos = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val damageCmp = cmDamage.get(entity)
        val bindCmp = cmBind.get(entity)
        val transCmp = cmTrans.get(bindCmp.entity)

        damageCmp.timer -= deltaTime
        if (damageCmp.timer <= 0f) {
            engine.removeEntity(entity)
            println("damage label entity removed")
        }
        try {
            vec3.set(transCmp.position)
            camera.project(vec3)
            nextPos.set(vec3.x, vec3.y)
            damageCmp.damageLabel.moveTo(nextPos)
        } catch (e: NullPointerException) {
            println("NPE on agent entity's pos fetch")
        }
    }
}